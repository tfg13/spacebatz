package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import java.util.LinkedList;

/**
 * Die EntityMap bietet performante Zugriffe auf Entities abhängig von ihrer Position.
 *
 * @author michael
 */
public class EntityMap {

    /**
     * Die Größe der Sektoren, in die die Map eingeteilt wird.
     */
    private final static double ENTITYMAP_SECTORSIZE = 10.0;
    /**
     * Die Karte der Sektoren, die das Level bilden
     */
    private EntityMapSector[][] sectors;
    /**
     * Die Breite des Levels
     */
    private double width;
    /**
     * die Höhe des Levels
     */
    private double height;

    /**
     * Konstruktor, initialisiert die EntityMap
     *
     * @param width die Breite des Levels
     * @param height die Höhe des Levels
     */
    public EntityMap(double width, double height) {
        this.height = height;
        this.width = width;
        // Die Größe des Levels durch die Sektorgröße, durch den Int-Cast abgerundet und mit +1 am Ende zum ausgleichen
        int xSectors = (int) (width / ENTITYMAP_SECTORSIZE) + 1;
        int ySectors = (int) (height / ENTITYMAP_SECTORSIZE) + 1;

        // Sektoren initialisieren:
        sectors = new EntityMapSector[xSectors][ySectors];
        for (int x = 0; x < xSectors; x++) {
            for (int y = 0; y < xSectors; y++) {
                sectors[x][y] = new EntityMapSector();
            }
        }

        // Nachbarsektoren registrieren:
        // Diese Schleife geht nur die Elemente
        for (int x = 0; x < xSectors; x++) {
            for (int y = 0; y < xSectors; y++) {
                EntityMapSector sector = sectors[x][y];
                sector.addNeighborSector(sector);// den Sektor selber in die Liste reinhaun
                for (int innerX = x - 1; innerX < x + 1; innerX++) {
                    for (int innerY = y - 1; innerY < y + 1; innerY++) {
                        if (0 < innerX && innerX < sectors.length && 0 < innerY && innerY < sectors[0].length) {
                            sector.addNeighborSector(sectors[innerX][innerY]);
                        }
                    }
                }
            }
        }
    }

    /**
     * Registriert eine neue Entity
     *
     * @param e die neue Entity
     */
    public void addEntity(Entity e) {
        if (e.getSector() != null) {
            throw new IllegalArgumentException("Diese Entity ist bereits in der EntityMap registriert!");
        }
        getSector(e.getX(), e.getY()).addEntity(e);
    }

    /**
     * Entfernt eine Entity aus der EntityMap
     *
     * @param e die Entity die entfernt werden soll
     */
    public void removeEntity(Entity e) {
        if (e.getSector() == null) {
            throw new IllegalArgumentException("Diese Entity ist gar nicht in der EntityMap registriert!");
        }
        getSector(e.getX(), e.getY()).removeEntity(e);
    }

    /**
     * MUSS aufgerufen werden, wenn sich eine Entity bewegt. Prüft, ob die Eintity ihren Sektor verlassen hat, und aktualisiert den Sektor falls ja.
     *
     * @param e die Entity die überprüft werden soll
     */
    public void entityMoved(Entity e) {
        if (e.getSector() == null) {
            throw new IllegalArgumentException("Diese Entity hat keinen Sektor! Sie muss zuerst mit EntityMap.addEntity() registriert werden!");
        }
        EntityMapSector newSector = getSector(e.getX(), e.getY());
        if (e.getSector().equals(newSector)) {
            e.getSector().removeEntity(e);
            newSector.addEntity(e);
            e.setSector(newSector);
        }
    }

    /**
     * Gibt eine Liste mit midestens allen Entities im Abstand von ENTITYMAP_SECTORSIZE vom angegebenen Punkt zurück.
     *
     * @param x die X-Koordinate der Position
     * @param y die Y-Koordinate der Position
     * @return eine Liste mit allen Entities nahe dem Punkt
     */
    public LinkedList<Entity> getEntitiesAroundPoint(double x, double y) {
        LinkedList<Entity> entities = new LinkedList<>();
        EntityMapSector thisSector = getSector(x, y);

        for (EntityMapSector s : thisSector.getMeAndMyNeighborSectors()) {
            entities.addAll(s.getEntities());
        }
        return entities;
    }

    /**
     * Gibt den Sektor, der die angegebene Position enthält, zurück
     *
     * @param x die X-Koordinate der Position
     * @param y die X-Koordinate der Position
     * @return der Sektor, der die Position enthält
     */
    private EntityMapSector getSector(double x, double y) {
        if (x < 0 || width < x || y < 0 || height < y) {
            throw new IllegalArgumentException("Point " + x + "/" + y + " Is outside the Level!");
        }
        double doubleX = (x / ENTITYMAP_SECTORSIZE);
        double doubleY = (y / ENTITYMAP_SECTORSIZE);
        int sectorX = (int) doubleX;
        int sectorY = (int) doubleY;
        return sectors[sectorX][sectorY];
    }
}
