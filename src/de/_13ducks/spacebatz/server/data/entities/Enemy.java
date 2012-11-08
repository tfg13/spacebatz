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
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.Distance;
import de._13ducks.spacebatz.util.Position;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps.
     */
    private int enemytypeID = 0;
    /**
     * Das Level dieses Gegners. Beeinflusst die möglichen Drops.
     */
    private int enemylevel;
    /**
     * Der Pfad dem der Gegner gerade folgt.
     * Enthält Start- und Zielfeld.
     */
    private Position[] path;
    /**
     * Der Index der Position in Pfad, von der der Gegner gerade kommt.
     */
    private int currentPathPosition;
    /**
     * Gibt an ob der Gegner gerade einem Pfad folgt.
     */
    private boolean followingPath;
    /**
     * Die Distanz zum nächsten Wegpunkt im Pfad.
     */
    private double distanceToNextWaypoint;

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
        speed = estats.getSpeed();
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

    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        if (isFollowingPath()) {
            // Zurückgelegte Distanz vom letzten Wegpunkt berechnen:
            double distance = Distance.getDistance(getX(), getY(), path[currentPathPosition].getX(), path[currentPathPosition].getY());
            // Wenn wir schon weiter sind als die Distanz zum nächsten Wegpunkt, dann setzten wir den übernächsten als Ziel:
            if (distanceToNextWaypoint - distance < 0.01 || distance >= distanceToNextWaypoint) {
                // auf den Wegpunkt snappen, da wir nicht ganz drauf stehen (das summiert sich sonst bei jedem Wegpunkt):
                setStopXY(path[currentPathPosition + 1].getX(), path[currentPathPosition + 1].getY());
                stopMovement();
                // den Nächsten Wegpunkt ansteuern:
                currentPathPosition++;
                // Anhalten, falls wir am Ende des Pfads sind:
                if (currentPathPosition == path.length - 1) {
                    followingPath = false;
                    path = null;
                    currentPathPosition = 0;
                } else {
                    setVector(path[currentPathPosition + 1].getX() - getX(), path[currentPathPosition + 1].getY() - getY());
                    // Distanz zum nächsten Wegpunkt berechnen:
                    double currentX = path[currentPathPosition].getX();
                    double currentY = path[currentPathPosition].getY();
                    double targetX = path[currentPathPosition + 1].getX();
                    double targetY = path[currentPathPosition + 1].getY();
                    distanceToNextWaypoint = Distance.getDistance(currentX, currentY, targetX, targetY);
                }
            }
        }
    }

    @Override
    public void onCollision(Entity other) {
        super.onCollision(other);
    }

    @Override
    public void onWallCollision() {
        super.onWallCollision();
        // Bei Kollision brechen wir das folgen ab:
        if (followingPath) {
            stopFollowongPath();
        }
    }

    /**
     * Lässt den Gegner einen Pfad entlang laufen.
     * Der Gegner geht davon aus das die erste Position im Pfad seine aktuelle Posiiton ist, er wird also direkt die 2. ansteuern.
     *
     * @param path der Pfad dem der Gegner folgen soll.
     */
    public void followPath(Position path[]) {
        if (path.length <= 1) {
            throw new IllegalArgumentException("Der übergebene Pfad muss mindestens 2 Elemente enthalten!");
        }
        // An den Anfang des Pfads springen:
        setVector(1, 1);
        setStopXY(path[0].getX(), path[0].getY());
        currentPathPosition = 0;
        this.path = path;
        followingPath = true;

        // Distanz zum nächsten Wegpunkt berechnen:
        double currentX = getX();
        double currentY = getY();
        double targetX = path[currentPathPosition + 1].getX();
        double targetY = path[currentPathPosition + 1].getY();
        distanceToNextWaypoint = Distance.getDistance(currentX, currentY, targetX, targetY);

        // Richtung erster wegpunkt gehen:
        setVector(path[1].getX() - getX(), path[1].getY() - getY());
    }

    /**
     * Hält den Gegner an, falls er gerade einem Pfad folgt.
     */
    public void stopFollowongPath() {
        if (followingPath) {
            stopMovement();
            followingPath = false;
            path = null;
            currentPathPosition = 0;
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
}
