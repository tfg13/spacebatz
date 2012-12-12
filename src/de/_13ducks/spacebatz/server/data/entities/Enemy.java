    /*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.astar.PrecisePosition;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.server.gamelogic.EnemySpawner;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.Distance;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char implements EntityLinearTargetObserver {

    /**
     * ID des Gegnertyps.
     */
    private int enemytypeID = 0;
    /**
     * Das Level dieses Gegners. Beeinflusst die möglichen Drops.
     */
    private int enemylevel;
    /**
     * Der Pfad dem der Gegner gerade folgt. Enthält Start- und Zielfeld.
     */
    private PrecisePosition[] path;
    /**
     * Der Index der Position in Pfad, von der der Gegner gerade kommt.
     */
    private int currentPathTarget;
    /**
     * Gibt an ob der Gegner gerade einem Pfad folgt.
     */
    private boolean followingPath;

    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param netid netID, nicht mehr änderbar.
     * @param typeid typeID gibt Gegnertyp an
     */
    public Enemy(double x, double y, int netid, int enemytypeID) {
        super(x, y, netid, (byte) 3);
        this.enemytypeID = enemytypeID;
        EnemyTypeStats estats = Server.game.enemytypes.getEnemytypelist().get(enemytypeID);
        getProperties().setHitpoints(estats.getHealthpoints());
        getProperties().setSightrange(estats.getSightrange());
        setSpeed(estats.getSpeed());
        this.enemylevel = estats.getEnemylevel();
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 4;
    }

    @Override
    public void netPack(byte[] b, int offset) {
        super.netPack(b, offset);
        Bits.putInt(b, super.byteArraySize() + offset, enemytypeID);
    }

    /**
     * @return the enemylevel
     */
    public int getEnemylevel() {
        return enemylevel;
    }

    /**
     * Lässt den Gegner einen Pfad entlang laufen. Der Gegner geht davon aus das die erste Position im Pfad seine aktuelle Posiiton ist, er wird also direkt die 2. ansteuern.
     *
     * @param path der Pfad dem der Gegner folgen soll.
     */
    public void followPath(PrecisePosition path[]) {
        if (path.length <= 1) {
            throw new IllegalArgumentException("Der übergebene Pfad muss mindestens 2 Elemente enthalten!");
        }
        if (followingPath) {
            stopFollowingPath();
        }

        for (int i = 0; i < path.length; i++) {
            for (int x = (int) (path[i].getX() - (getSize() / 2)); x < (int) (path[i].getX() + (getSize() / 2)); x++) {
                for (int y = (int) (path[i].getY() - (getSize() / 2)); y < (int) (path[i].getY() + (getSize() / 2)); y++) {
                    if (Server.game.getLevel().getCollisionMap()[x][y]) {
                        System.out.println("Illegal Path position at " + x + " " + y);
                    }
                }
            }
        }

        followingPath = true;
        this.path = path;
        currentPathTarget = 0;
        setLinearTarget(path[currentPathTarget].getX(), path[currentPathTarget].getY(), this);

    }

    /**
     * Hält den Gegner an, falls er gerade einem Pfad folgt.
     */
    public void stopFollowingPath() {
        if (followingPath) {
            followingPath = false;
            stopMovement();
            path = null;
            currentPathTarget = -1;
        } else {
            throw new IllegalStateException("Cant stop following path while not folling path!");
        }
    }

    /**
     * Gibt an ob dieser Gegner gerade einem Pfad folgt.
     *
     * @return the followingPath
     */
    public boolean isFollowingPath() {
        return followingPath;
    }

    @Override
    public void decreaseHitpoints(int damage) {
        super.decreaseHitpoints(damage);
        if (properties.getHitpoints() < 0) {
            EnemySpawner.notifyEnemyDeath();
            Server.game.getEntityManager().removeEntity(netID);
            DropManager.dropItem(getX(), getY(), enemylevel);
        }
    }

    @Override
    public void targetReached() {
        if (followingPath) {
            if (currentPathTarget + 1 < path.length) {
                currentPathTarget++;
                setLinearTarget(path[currentPathTarget].getX(), path[currentPathTarget].getY(), this);
            }
        } else {
            throw new IllegalStateException("Reached target while not following a path!");
        }

    }

    @Override
    public void movementBlocked() {
        System.out.println("blocked at " + getX() + " " + getY() + " . path: ");
        for (int i = 0; i < path.length; i++) {
            System.out.println(path[i].getX() + " " + path[i].getY());
        }
    }

    @Override
    public void movementAborted() {
        System.out.println("movement aborted!");
    }
}
