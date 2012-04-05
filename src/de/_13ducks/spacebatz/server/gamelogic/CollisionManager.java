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
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.*;
import de._13ducks.spacebatz.server.data.abilities.HitscanAbility;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Distance;
import de._13ducks.spacebatz.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Berechnet Kollisionen zwischen Chars, Enemys und Bullets.
 *
 * @author michael
 */
public class CollisionManager {

    /**
     * Der Radius, in dem Entitys für Kolliisonsberechnung gesucht werden @TODO: gescheiten Namen finden
     */
    private static double HARDCODEDCOLLISIONAROUNDMERADIUS = 2.0;

    /**
     * Berechnet Kollision für alle Entities
     */
    public static void computeCollision() {
        computeBulletCollision();
        computeWallCollision();
        computeMobCollission();
        computeItemCollission();
    }

    /**
     * Berechnet Kollisionen zwischen Bullets und Chars
     */
    private static void computeBulletCollision() {

        Iterator<Entity> listIterator = Server.game.netIDMap.values().iterator();

        while (listIterator.hasNext()) {
            Bullet bullet;
            Entity entity = listIterator.next();
            if (entity instanceof Bullet) {
                bullet = (Bullet) entity;
            } else {
                continue;
            }


            // Bullet muss nach bestimmter Zeit gelöscht werden
            if (Server.game.getTick() > bullet.getDeletetick()) {
                listIterator.remove();
                Server.entityMap.removeEntity(bullet);

                continue;
            }



            double x = bullet.getX();
            double y = bullet.getY();

            // Provisorische Kollision von Bullets mit zerstörbaren Blöcken:
            if (Server.game.getLevel().isBlockDestroyable((int) x, (int) y)) {
                Server.game.getLevel().destroyBlock((int) x, (int) y);
            }

            Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(x, y, HARDCODEDCOLLISIONAROUNDMERADIUS).iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (e instanceof Char) {
                    Char c = (Char) e;
                    if (Math.abs(x - c.getX()) < 0.7 && Math.abs(y - c.getY()) < 0.7) {
                        HitManager.charBulletCollision(c, bullet);
                    }
                }
            }
        }
    }

//    /**
//     * Berechnet Flächenschaden von Bullet-Explosionen Bekommt den Char übergeben, den es direkt getroffen hat, damit dieser nicht nochmal Schaden
//     * kriegt
//     */
//    private static void computeBulletExplosionCollision(Bullet bullet, Char charhit) {
//        double x = bullet.getX();
//        double y = bullet.getY();
//
//        Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(x, y, bullet.getExplosionradius()).iterator();
//        while (iter.hasNext()) {
//            Entity e = iter.next();
//            if (e instanceof Char) {
//                Char c = (Char) e;
//                if (c == charhit) {
//                    continue;
//                }
//                double distance = Math.sqrt((x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()));
//
//                if (!c.equals(bullet.getOwner())) {
//                    if (c instanceof Enemy) {
//                        Enemy en = (Enemy) c;
//                        // Schaden von HP abziehen, wird von Distanz verringert
//                        if (en.decreaseHealthpoints(bullet, 1.0 - distance / bullet.getExplosionradius() * 2 / 3)) {
//                        } else {
//                            if (en.getMyTarget() == null) {
//                                en.setMyTarget(bullet.getOwner());
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//    }
    /**
     * Berechnet Kollisionen zwischen Wänden und Chars
     */
    private static void computeWallCollision() {
        // Alle Chars, die sich bewegen auf Kollision prüfen:
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char mover = (Char) e;
                if (mover.isMoving()) {
                    // Startpunkt der Bewegung:
                    double fromX = mover.getX();
                    double fromY = mover.getY();
                    // Zielpunkt der Bewegung:
                    double toX = mover.extrapolateX(1);
                    double toY = mover.extrapolateY(1);
                    computeCharCollision(fromX, fromY, toX, toY, mover);


                }
            }
        }
    }

    private static void computeCharCollision(double fromX, double fromY, double toX, double toY, Char mover) {
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - mover.getProperty("size") / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + mover.getProperty("size") / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - mover.getProperty("size") / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + mover.getProperty("size") / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (Settings.DOUBLE_EQUALS_DIST + 0.5 + mover.getProperty("size") / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (Settings.DOUBLE_EQUALS_DIST + 0.5 + mover.getProperty("size") / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((mover.getProperty("size") / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                    }
                }
            }
        }
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann ohne kollision:
            double newX = fromX + smallestD * deltaX;
            mover.setStillX(newX);
        }

        // Werte zurücksetzen
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
                    d1 = ((blockMidY + (Settings.DOUBLE_EQUALS_DIST + 0.5 + mover.getProperty("size") / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (Settings.DOUBLE_EQUALS_DIST + 0.5 + mover.getProperty("size") / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((mover.getProperty("size") / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                    }
                }
            }
        }
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann
            mover.setStillY(fromY + smallestD * deltaY);
        }


        for (int testX = (int) mover.getX() - 3; testX < (int) mover.getX() + 3; testX++) {
            for (int testY = (int) mover.getY() - 3; testY < (int) mover.getY() + 3; testY++) {
                if (Server.game.getLevel().getCollisionMap()[testX][testY] == true) {
                    double testDX = Math.abs((testX + 0.5) - mover.getX());
                    double testDY =  Math.abs((testY + 0.5) - mover.getY());
                    double testD = (0.5) + mover.getProperty("size") / 2;
                    if (testDX < testD && testDY < testD) {
                        System.out.println("KOLLISION!!!!!!!");
                    }
                }
            }
        }




    }

    /**
     * Berechnet Kollision mit Mobs
     */
    private static void computeMobCollission() {
        // Alle Chars, die sich bewegen auf Kollision prüfen:
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char mover = (Char) e;
                if (mover instanceof Player) {
                    Iterator<Entity> iter2 = Server.entityMap.getEntitiesAroundPoint(e.getX(), e.getY(), HARDCODEDCOLLISIONAROUNDMERADIUS).iterator();
                    while (iter2.hasNext()) {
                        Entity e2 = iter2.next();
                        if (e2 instanceof Enemy) {
                            Enemy mob = (Enemy) e2;
                            double distance = Distance.getDistance(mover.getX(), mover.getY(), mob.getX(), mob.getY());
                            if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                                mob.attack(mover);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Berechnet Kollision mit Items
     */
    private static void computeItemCollission() {
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Player) {
                Player collector = (Player) e;
                Iterator<Item> iterator = Server.game.getItemMap().values().iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    double distance = Distance.getDistance(collector.getX(), collector.getY(), item.getPosX(), item.getPosY());
                    if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                        if (distance < Settings.SERVER_COLLISION_DISTANCE) {
                            if (item.getName().equals("Money")) {
                                collector.setMoney(collector.getMoney() + item.getAmount());
                                iterator.remove();
                                Server.msgSender.sendItemGrab(item.getNetID(), collector.getClient().clientID);
                            } else if (collector.getItems().size() < Settings.INVENTORY_SIZE) {
                                collector.putItem(item.getNetID(), item);
                                iterator.remove();
                                Server.msgSender.sendItemGrab(item.getNetID(), collector.getClient().clientID);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Berechnet Hitscankollision, ruft für die getroffenen Gegner den Hitmanager auf
     *
     * @param x
     * @param y
     * @param angle
     * @param range
     */
    public static ArrayList<Char> computeHitscanCollision(Char owner, double angle, double range, HitscanAbility hitscanAbility) {
        ArrayList<Char> charsHit = new ArrayList<>();

        double x = owner.getX();
        double y = owner.getY();

        double otherangle = angle - (Math.PI / 2);
        if (otherangle < 0) {
            otherangle += 2 * Math.PI;
        }

        Vector apos = new Vector(x, y);
        Vector adir = new Vector(Math.cos(angle), Math.sin(angle));
        Vector bdir = new Vector(Math.cos(otherangle), Math.sin(otherangle));

        Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(x, y, range).iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char c = (Char) e;

                if (c.equals(owner)) {
                    continue;
                }

                Vector bpos = new Vector(c.getX(), c.getY());

                // Schnittpunkt der 2 Geraden
                Vector s = adir.intersectionWith(apos, bpos, bdir);

                double distance = Math.sqrt((s.getX() - c.getX()) * (s.getX() - c.getX()) + (s.getY() - c.getY()) * (s.getY() - c.getY()));

                // Hitscan-Gerade nah genug am Gegner?
                if (distance < Settings.CHARSIZE / 2) {
                    // Nicht hinter dem Abilityuser?
                    double dx = s.getX() - x;
                    double dy = s.getY() - y;
                    double testangle = Math.atan2(dy, dx); // zwischen 0 und 2 pi
                    if (testangle < 0) {
                        testangle += 2 * Math.PI;
                    }

                    if (testangle < angle + Math.PI / 2 && testangle > angle - Math.PI / 2) {
                        charsHit.add(c);
                    }
                }
            }
        }
        return charsHit;
    }
}
