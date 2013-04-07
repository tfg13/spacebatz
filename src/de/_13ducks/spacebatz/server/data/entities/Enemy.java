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
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.server.gamelogic.EnemySpawner;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char implements EntityLinearTargetObserver, PathRequester {

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
    public void followPath(Vector path[]) {
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
        setLinearTarget(path[currentPathTarget].x, path[currentPathTarget].y, this);

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
        if (properties.getHitpoints() <= 0) {
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
                setLinearTarget(path[currentPathTarget].x, path[currentPathTarget].y, this);
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
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - getSize() / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + getSize() / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - getSize() / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + getSize() / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Den Block, mit dem wir kollidieren zwischenspeichern
        int[] collisionBlock = new int[2];
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
                        }
                    }
                }
            }
        }
        double sx = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann ohne kollision:
            sx = fromX + smallestD * deltaX;
        }

        // Für die Y-Berechung die Werte zurücksetzten, für die Block-Berechung aber behalten!
        double globalsmallestD = smallestD;
        smallestD = Double.MAX_VALUE;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                        // Näher als die von X?
                        if (d < globalsmallestD) {
                            globalsmallestD = d;
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
                        }
                    }
                }
            }
        }
        double sy = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann
            sy = fromY + smallestD * deltaY;
        }

        // Bewegung koorigieren?
        if (!(Double.isNaN(sx) && Double.isNaN(sy))) {
            return false;
        } else {
            return true;
        }

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
}
