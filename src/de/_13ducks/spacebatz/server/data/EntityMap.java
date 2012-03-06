package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Distance;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Die EntityMap bietet performante Zugriffe auf Entities abhängig von ihrer Position.
 *
 * @author michael
 */
public class EntityMap {

    /**
     * Die Größe eines Sektors der EntityMap
     */
    private final static double SECTORSIZE = 1.0;
    /**
     * Die Listen mit den Einheiten im jeweiligen Sektor
     */
    private LinkedList<Entity>[][] sectors;
    /**
     * Die Breite der EntityMap
     */
    private int entityMapWidth;
    /**
     * Die Höhe der EntityMap
     */
    private int entityMapHeight;

    /**
     * Konstruktor, initialisiert die Sektoren
     *
     * @param levelWidth die Breite des Levels
     * @param levelHeight die Höhe des Levels
     */
    public EntityMap(int levelWidth, int levelHeight) {
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
     * Aktualisiert die Positionen der Entitys
     */
    public void calculateEntityPositions() {
        Iterator<Entity> iterator = Server.game.netIDMap.values().iterator();

        while (iterator.hasNext()) {
            Entity e = iterator.next();
            if (e.isMoving()) {
                if (0 < e.getX() && e.getX() < entityMapWidth && 0 < e.getY() && e.getY() < entityMapHeight) {
                    if (!getSector(e.getX(), e.getY()).contains(e)) {
                        removeEntity(e);
                        insertEntity(e);
                    }
                } else {
					// Entity ganz aus dem Spiel löschen, wenn aus der Map raus.
					removeEntity(e);
					Server.game.netIDMap.remove(e.netID);
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
     * Registriert eine Entity in der EntityMap ein
     *
     * @param entity die Entity die eingefügt wird
     */
    public void insertEntity(Entity entity) {
        if (0 < entity.getX() && entity.getX() < entityMapWidth && 0 < entity.getY() && entity.getY() < entityMapHeight) {
            getSector(entity.getX(), entity.getY()).add(entity);
        } else {
            throw new RuntimeException("Cannot add Entity " + entity.netID + " to EntityMap, it has left the map (Position: " + entity.getX() + "/" + entity.getY() + ")!");
        }

    }

    /**
     * Entfernt eine Entity aus der EntityMap.
     *
     * @param entity die Entity die entfernt werden soll
     */
    public void removeEntity(Entity entity) {
        int x1 = (int) (entity.getX() / SECTORSIZE) - 1;
        int y1 = (int) (entity.getX() / SECTORSIZE) - 1;

        // Entity in den Nachbarsektoren ihres letzten bekannten Sektors suchen und löschen:
        boolean foundAndRemoved = false;
        for (int x = x1; x < x1 + 2; x++) {

            for (int y = y1; y < y1 + 2; y++) {
                if (x > 0 && x < entityMapWidth && y > 0 && y < entityMapHeight) {
                    if (sectors[x][y].contains(entity)) {
                        sectors[x][y].remove(entity);
                        foundAndRemoved = true;
                    }
                }

            }
        }
        // Wenn sie da nicht gefunden wurde die ganze EntityMap durchsuchen:
        if (!foundAndRemoved) {
            for (int x = 0; x < entityMapWidth; x++) {
                for (int y = 0; y < entityMapHeight; y++) {
                    if (sectors[x][y].contains(entity)) {
                        sectors[x][y].remove(entity);
                        foundAndRemoved = true;
                    }
                }
            }
        }
        // Wenn sie nicht in der EntityMap registriert ist werfen wir eine Exception:
        if (!foundAndRemoved) {
            throw new RuntimeException("Cannot remove Entity " + entity.netID + " from the EntityMap, it is not registered!");
        }

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
    private LinkedList<Entity> getEntitiesInArea(int x1, int y1, int x2, int y2) {
        LinkedList<Entity> entities = new LinkedList<>();

        for (int x = x1; x < x2; x++) {
            for (int y = y1; y < y2; y++) {
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
