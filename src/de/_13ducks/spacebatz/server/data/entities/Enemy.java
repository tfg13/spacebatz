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
import de._13ducks.spacebatz.server.ai.astar.PathRequester;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.entities.move.InterpolatedMover;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.shared.Collision;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_FACING_TARGET;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char implements EntityLinearTargetObserver, PathRequester {

    /**
     * Das Bewegungssystem dieses Gegeners. Gegner werden immer vom Server
     * gesteuert, verwenden deshalb InterpolatedMover
     */
    public final InterpolatedMover move;
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
    private Vector[] path;
    /**
     * Der Index der Position in Pfad, von der der Gegner gerade kommt.
     */
    private int currentPathTarget;
    /**
     * Gibt an ob der Gegner gerade einem Pfad folgt.
     */
    private boolean followingPath;
    /**
     * Die primäre Angriffsfähigkeit des Gegners.
     */
    private Ability shootAbility;
    /**
     * The enemys AI.
     */
    private Behaviour behaviour;
    /**
     * Der GameTick, in dem zuletzt Sichtkontakt zum Ziel war.
     */
    private int lastSightContact;
    public double maxSpeed;
    private boolean isFacingTarget;
    private int facingTargetNetId;
    public Player target;

    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param netid netID, nicht mehr änderbar.
     * @param typeid typeID gibt Gegnertyp an
     */
    public Enemy(double x, double y, int netid, int enemytypeID, Team team) {
        super(netid, (byte) 3, new InterpolatedMover(x, y), team);
        this.move = (InterpolatedMover) super.move;
        move.setEntity(this);
        this.enemytypeID = enemytypeID;
        EnemyTypeStats estats = (new EnemyTypes()).getEnemytypelist().get(enemytypeID);
        getProperties().setHitpoints(estats.healthpoints);
        getProperties().setMaxHitpoints(estats.healthpoints);
        getProperties().setSightrange(estats.sightrange);
        setSpeed(estats.speed);
        maxSpeed = estats.speed;
        this.enemylevel = estats.enemylevel;
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 9;
    }

    @Override
    public void netPack(byte[] b, int offset) {
        super.netPack(b, offset);
        Bits.putInt(b, super.byteArraySize() + offset, enemytypeID);
        Bits.putChar(b, super.byteArraySize() + offset + 4, (isFacingTarget ? '1' : '0'));
        Bits.putInt(b, super.byteArraySize() + offset + 5, facingTargetNetId);
    }

    /**
     * @return the enemylevel
     */
    public int getEnemylevel() {
        return enemylevel;
    }

    /**
     * Lässt den Gegner einen Pfad entlang laufen. Der Gegner geht davon aus das
     * die erste Position im Pfad seine aktuelle Posiiton ist, er wird also
     * direkt die 2. ansteuern.
     *
     * @param path der Pfad dem der Gegner folgen soll.
     */
    public void followPath(Vector path[]) {
        setSpeed(maxSpeed);
        if (path.length <= 1) {
            throw new IllegalArgumentException("Der übergebene Pfad muss mindestens 2 Elemente enthalten!");
        }
        if (followingPath) {
            stopFollowingPath();
        }

        for (int i = 0; i < path.length; i++) {
            for (int x = (int) (path[i].x - (getSize() / 2)); x <= (int) (path[i].x + (getSize() / 2)); x++) {
                for (int y = (int) (path[i].y - (getSize() / 2)); y <= (int) (path[i].y + (getSize() / 2)); y++) {
                    if (Server.game.getLevel().getCollisionMap()[x][y]) {
                        System.out.println("Illegal Path position at " + x + " " + y);
                    }
                }
            }
        }


        // Eventuell liegt der Anfang des Pfads hinter uns, weil wir uns bewegt haben bevor er fertig berechnet war.
        // Also auf das vorderste per Luftlinie erreichbare Feld gehen:
        currentPathTarget = 0;
        for (int i = path.length - 1; i >= 0; i--) {
            if (lineOfSight(getX(), getY(), path[i].x, path[i].y)) {
                currentPathTarget = i;
                break;
            }
        }
        followingPath = true;
        this.path = path;
        move.setLinearTarget(path[currentPathTarget].x, path[currentPathTarget].y, this);

    }

    /**
     * Hält den Gegner an, falls er gerade einem Pfad folgt.
     */
    public void stopFollowingPath() {
        if (followingPath) {
            followingPath = false;
            move.stopMovement();
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
        if (properties.getHitpoints() <= 0) {
            dead = true;
            Server.game.getEntityManager().removeEntity(netID);
            if (target != null) {
                target.hunters.remove(this);
            }
            DropManager.dropItem(getX(), getY(), enemylevel);
        }
    }

    @Override
    public void targetReached() {
        if (followingPath) {
            if (currentPathTarget + 1 < path.length) {
                currentPathTarget++;
                move.setLinearTarget(path[currentPathTarget].x, path[currentPathTarget].y, this);
            }
        } else {
            behaviour = behaviour.getTargetReachedBehaviour();
        }

    }

    @Override
    public void movementBlocked() {
        behaviour = behaviour.getMovementBlockedBehaviour();
    }

    @Override
    public void movementAborted() {
        behaviour = behaviour.getMovementAbortedBehaviour();
    }

    @Override
    public void onCollision(Entity other) {
        behaviour = behaviour.onCollision(other);
    }

    /**
     * Set the Behaviour of this enemy.
     *
     * @param behaviour
     */
    public void setBehaviour(Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * Gibt an, ob dieser Gegner Luftlinie zur Zielposition laufen könnte.
     *
     * @param fromX
     * @param fromY
     * @param toX
     * @param toY
     * @return
     */
    public boolean lineOfSight(double fromX, double fromY, double toX, double toY) {
        return !Collision.computeCollision(fromX, fromY, toX, toY, getSize(), Server.game.getLevel().getCollisionMap()).collides;
    }

    @Override
    public void pathComputed(Vector[] path) {
        behaviour = behaviour.pathComputed(path);
    }

    @Override
    public void tick(int gametick) {
        super.tick(gametick);
        behaviour = behaviour.tick(gametick);
    }

    /**
     * @return the shootAbility
     */
    public Ability getShootAbility() {
        return shootAbility;
    }

    /**
     * @param shootAbility the shootAbility to set
     */
    public void setShootAbility(Ability shootAbility) {
        this.shootAbility = shootAbility;
    }

    /**
     * @return the lastSightContact
     */
    public int getLastSightContact() {
        return lastSightContact;
    }

    /**
     * @param lastSightContact the lastSightContact to set
     */
    public void setLastSightContact(int lastSightContact) {
        this.lastSightContact = lastSightContact;
    }

    public void targetDied() {
        this.behaviour = behaviour.onTargetDeath();
        stopFacingTarget();
    }

    /**
     * Lässt diesen Gegner immer auf das angegebene Ziel schauen, egal wie er
     * sich bewegt.
     *
     * @param target
     */
    public void setFacingTarget(Entity target) {
        isFacingTarget = true;
        facingTargetNetId = target.netID;
        STC_SET_FACING_TARGET.sendSetFacingTarget(isFacingTarget, this.netID, facingTargetNetId);

    }

    /**
     * Lässt den Gegner wieder normal schauen.
     */
    public void stopFacingTarget() {
        STC_SET_FACING_TARGET.sendSetFacingTarget(false, this.netID, -1);
    }

    public void setAttackTarget(Player target) {
        target.hunters.add(this);
        behaviour = behaviour.onAttackTarget(target);
    }
}
