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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.util.geo.Distance;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Das FastFindGrid bietet performante Zugriffe auf Entities abhängig von ihrer Position.
 *
 * @author michael
 */
public class FastFindGrid {

    /**
     * Die Größe eines Sektors des FastFindGrids
     */
    private final static double SECTORSIZE = 1.0;
    /**
     * Die Listen mit den Einheiten im jeweiligen Sektor
     */
    private LinkedList<Entity>[][] sectors;
    /**
     * Die Breite des FastFindGrids
     */
    private int entityMapWidth;
    /**
     * Die Höhe des FastFindGrids
     */
    private int entityMapHeight;

    /**
     * Konstruktor, initialisiert die Sektoren
     *
     * @param levelWidth die Breite des Levels
     * @param levelHeight die Höhe des Levels
     */
    public FastFindGrid(int levelWidth, int levelHeight) {
        entityMapWidth = (int) (levelWidth / SECTORSIZE) + 1;
        entityMapHeight = (int) (levelHeight / SECTORSIZE) + 1;

        sectors = new LinkedList[entityMapWidth][entityMapHeight];

        for (int x = 0; x < entityMapWidth; x++) {
            for (int y = 0; y < entityMapHeight; y++) {
                sectors[x][y] = new LinkedList<>();
            }
        }
    }

    /**
     * Aktualisiert die Positionen des FastFindGrids
     */
    public void calculateEntityPositions() {
        Iterator<Entity> iterator = Server.game.getEntityManager().getEntityIterator();

        while (iterator.hasNext()) {
            Entity e = iterator.next();
            if (e.isMoving()) {
                if (0 < e.getX() && e.getX() < entityMapWidth * SECTORSIZE && 0 < e.getY() && e.getY() < entityMapHeight * SECTORSIZE) {
                    if (!getSector(e.getX(), e.getY()).contains(e)) {
                        removeEntity(e);
                        insertEntity(e);
                    }
                } else {
                    // Entity ganz aus dem Spiel löschen, wenn aus der Map raus.
                    removeEntity(e);
                    Server.game.getEntityManager().removeEntity(e.netID);
                    throw new IllegalStateException("Entity " + e.netID + " ist jetzt bei " + e.getX() + " " + e.getY() + " und hat damit die Map verlassen!");
                }
            }
        }
    }

    /**
     * Gibt alle Entities in einem bestimmten Abstand um einen Punkt zurück.
     *
     * @param x X-Koordinate des Mittelpunktes
     * @param y Y-Koordinate des Mittelpunktes
     * @param radius der Radius, in dem gesucht wird
     * @return eine Liste mit Entities im angegebenen Radius um den Punkt
     */
    public LinkedList<Entity> getEntitiesAroundPoint(double x, double y, double radius) {
        LinkedList<Entity> entities = getEntitiesInArea((int) (x - radius), (int) (y - radius), (int) (x + radius) + 1, (int) (y + radius) + 1);

        Iterator<Entity> iter = entities.iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (Distance.getDistance(e.getX(), e.getY(), x, y) > radius) {
                iter.remove();
            }
        }
        return entities;
    }

    /**
     * Registriert eine Entity im FastFindGrids.
     *
     * @param entity die Entity die eingefügt wird
     */
    public void insertEntity(Entity entity) {
        if (0 < entity.getX() && entity.getX() < entityMapWidth * SECTORSIZE && 0 < entity.getY() && entity.getY() < entityMapHeight * SECTORSIZE) {
            getSector(entity.getX(), entity.getY()).add(entity);
            entity.getEntityMapPos()[0] = (int) (entity.getX() / SECTORSIZE);
            entity.getEntityMapPos()[1] = (int) (entity.getY() / SECTORSIZE);
        } else {
            throw new RuntimeException("Cannot add Entity " + entity.netID + " to EntityMap, it has left the map (Position: " + entity.getX() + "/" + entity.getY() + ")!");
        }

    }

    /**
     * Entfernt eine Entity aus dem FastFindGrid.
     *
     * @param entity die Entity die entfernt werden soll
     */
    public void removeEntity(Entity entity) {
        sectors[entity.getEntityMapPos()[0]][entity.getEntityMapPos()[1]].remove(entity);
    }

    /**
     * Gibt den Sektor, in dem eine Position liegt, zurück
     *
     * @param x X-Koordinate der Position
     * @param y Y-Koordinate der Position
     * @return der Sektor, der die Position enthält
     */
    private LinkedList<Entity> getSector(double x, double y) {
        return sectors[(int) (x / SECTORSIZE)][(int) (y / SECTORSIZE)];
    }

    /**
     * Gibt eine Liste mit allen Einheiten im Angegebenen Gebiet zurück.
     *
     * @param x1 X-Koordinate der linken oberen Ecke des Gebiets
     * @param y1 Y-Koordinate der linken oberen Ecke des Gebiets
     * @param x2 X-Koordinate der rechten unteren Ecke des Gebiets
     * @param y2 Y-Koordinate der rechten unteren Ecke des Gebiets
     * @return eine Liste mit allen registrierten Entities in dem Gebiet
     */
    public LinkedList<Entity> getEntitiesInArea(int x1, int y1, int x2, int y2) {
        LinkedList<Entity> entities = new LinkedList<>();

        int sectorX1 = (int) (x1 / SECTORSIZE);
        int sectorY1 = (int) (y1 / SECTORSIZE);
        int sectorX2 = (int) (x2 / SECTORSIZE);
        int sectorY2 = (int) (y2 / SECTORSIZE);

        for (int x = sectorX1; x < sectorX2; x++) {
            for (int y = sectorY1; y < sectorY2; y++) {
                if (0 < x && x < entityMapWidth && 0 < y && y < entityMapHeight) {
                    for (Entity e : sectors[x][y]) {
                        entities.add(e);
                    }
                }

            }
        }
        return entities;
    }
}
