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
import de._13ducks.spacebatz.server.data.abilities.HitscanAbility;
import de._13ducks.spacebatz.server.data.entities.*;
import de._13ducks.spacebatz.util.Distance;
import de._13ducks.spacebatz.util.Position;
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
     * Der Radius, in dem Entitys für Kolliisonsberechnung gesucht werden
     *
     * @TODO: gescheiten Namen finden
     */
    private static double HARDCODEDCOLLISIONAROUNDMERADIUS = 2.0;

    /**
     * Berechnet Kollision für alle Entities
     */
    public static void computeCollision() {
        computeBulletCollision();
        computeMobCollission();
    }

    /**
     * Berechnet Kollisionen zwischen Bullets und Chars
     */
    private static void computeBulletCollision() {

        Iterator<Entity> listIterator = Server.game.getEntityManager().getEntityIterator();

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
                Server.game.getEntityManager().removeEntity(bullet.netID);
                continue;
            }

            double x = bullet.getX();
            double y = bullet.getY();

            Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(x, y, HARDCODEDCOLLISIONAROUNDMERADIUS).iterator();
            while (iter.hasNext()) {
                Entity e = iter.next();
                if (e instanceof Char) {
                    Char c = (Char) e;
                    if (Math.abs(x - c.getX()) < 0.7 && Math.abs(y - c.getY()) < 0.7) {
                        // Wenn der Char das Bullet nicht selber erzeugt hat:
                        if (!bullet.getOwner().equals(c)) {
                            bullet.onCollision(c);
                            c.onCollision(bullet);
                            break;
                        }

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
        Iterator<Entity> iter = Server.game.getEntityManager().getEntityIterator();
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
                                mob.onCollision(mover);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Berechnet Hitscankollision, gibt alle getroffenen Chars zurück
     *
     * @param owner Char, der die Ability ausführt
     * @param angle Richtung
     * @param range Reichweite des Angriffs
     */
    public static ArrayList<Char> computeHitscanOnChars(Char owner, double angle, double range, HitscanAbility hitscanAbility) {
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

    /**
     * Gibt die Position des nächsten Felds mit Kollision zurück, das getroffen wird, oder null
     *
     * @param owner Char, der die Ability ausführt
     * @param angle Richtung
     * @param range Reichweite der Ability
     * @return
     */
    public static Position computeHitscanOnBlocks(Char owner, double angle, double range) {
        ArrayList<Position> positionsInHitscan = new ArrayList<>();

        double betaX = owner.getX() + range * Math.cos(angle);
        double betaY = owner.getY() + range * Math.sin(angle);

        double vX = betaX - owner.getX();
        double vY = betaY - owner.getY();
        if (Math.abs(vX) >= Math.abs(vY)) {
            if (vX > 0) {
                for (int i = 0; i < vX; i++) {
                    Position argh = new Position((int) owner.getX() + i, (int) (owner.getY() + (i * vY / vX)));
                    positionsInHitscan.add(argh);
                }
            } else {
                for (int i = 0; i > vX; i--) {
                    Position argh = new Position((int) owner.getX() + i, (int) (owner.getY() + (i * vY / vX)));
                    positionsInHitscan.add(argh);
                }
            }
        } else {
            if (vY > 0) {
                for (int i = 0; i < vY; i++) {
                    Position argh = new Position((int) (owner.getX() + (i * vX / vY)), (int) owner.getY() + i);
                    positionsInHitscan.add(argh);
                }
            } else {
                for (int i = 0; i > vY; i--) {
                    Position argh = new Position((int) (owner.getX() + (i * vX / vY)), (int) owner.getY() + i);
                    positionsInHitscan.add(argh);
                }
            }
        }

        double mindistance = Double.MAX_VALUE;
        int nearestblock = -1;
        for (int i = 0; i < positionsInHitscan.size(); i++) {

            int bx = positionsInHitscan.get(i).getX();
            int by = positionsInHitscan.get(i).getY();
            if (Server.game.getLevel().getCollisionMap()[bx][by]) {
                double distance = Math.sqrt((owner.getX() - bx) * (owner.getX() - bx) + (owner.getY() - by) * (owner.getY() - by));
                if (distance < mindistance) {
                    mindistance = distance;
                    nearestblock = i;
                }
            }
        }

        if (nearestblock > -1) {
            return positionsInHitscan.get(nearestblock);
        } else {
            return null;
        }
    }
}
