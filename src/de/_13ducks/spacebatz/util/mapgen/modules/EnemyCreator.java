package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.gamelogic.EnemyFactory;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.PolyMesh;
import de._13ducks.spacebatz.util.geo.Vector;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Bevölkert die Map mit Gegnern vor.
 * Jemand anderes muss dafür vorher schon in die MPolygone reingeschrieben haben, wieviele Gegener von welchem Typ
 * gespawnt werden sollen.
 *
 * Dieses Modul spawnt diese Gegner dann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class EnemyCreator extends Module {

    private InternalMap map;

    @Override
    public String getName() {
        return "enemycreator";
    }

    @Override
    public boolean requiresSeed() {
        return true;
    }

    @Override
    public String[] provides() {
        return new String[0];
    }

    @Override
    public boolean computesPolygons() {
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{"ENEMYDEF"};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        this.map = map;
        Random random = new Random(Long.parseLong(parameters.get("SEED")));
        traversePolyMesh(map.polygons, random);
    }

    /**
     * Traversiert ein Polygonnetz mit einer Tiefensuche.
     *
     * @param root die Wurzel dieser Rekursionsstufe
     * @param random der Zufallsgenerator
     */
    private void traversePolyMesh(PolyMesh root, Random random) {
        Iterator<MPolygon> polyIter = root.polyIter();
        while (polyIter.hasNext()) {
            MPolygon poly = polyIter.next();
            if (poly.getMesh() != null) {
                traversePolyMesh(poly.getMesh(), random);
            }
            computePoly(poly, random);
        }
    }

    /**
     * Verarbeitet den gegebenen Polygon.
     * Wird für jeden Polygon, der spawninfos enthält genau ein mal aufgerufen.
     *
     * @param zone der zu bevölkernde Polygon
     * @param random der Zufallsgenerator
     */
    private void computePoly(MPolygon poly, Random random) {
        // Zur Auswahl stehende Felder berechnen:
        List<Vector> spawnLocations = calcSpawnLocations(poly);
        // Zonen die bevölkert werden sollen, haben eine Menge von Gegnertypen, die da spawnen sollen
        HashMap<Integer, Integer> spawnInfo = poly.spawnInfo;
        if (spawnInfo != null) {
            // Spawnen
            for (Integer type : spawnInfo.keySet()) {
                int number = spawnInfo.get(type);
                // Jetzt so viele Gegner spawnen:
                for (int i = 0; i < number; i++) {
                    if (spawnLocations.isEmpty()) {
                        System.out.println("WARN: MAPGEN: ENEMYCREATOR: Cannot place (all) enemys in " + poly + ", insufficient space");
                    }
                    // Zufallsposition im Polygon
                    Vector position = spawnLocations.get(random.nextInt(spawnLocations.size()));
                    spawnLocations.remove(position);
                    spawnLocations.remove(position.substract(Vector.ONE_X));
                    spawnLocations.remove(position.substract(Vector.ONE_Y));
                    spawnLocations.remove(position.substract(Vector.ONE_XY));
                    Enemy enemy = EnemyFactory.createEnemy(position.x, position.y, map.getNextNetId(), type);
                    map.startEntitys.put(enemy.netID, enemy);
                }
            }
        }
    }

    /**
     * Berechnet alle möglichen Spawnpunkte für ein MPolygon
     *
     * @param poly der Polygon
     * @return List mit möglichen Spawnpunkten
     */
    private List<Vector> calcSpawnLocations(MPolygon poly) {
        ArrayList<Vector> spawnPositions = new ArrayList<>();
        // Alle Felder einfügen, die das Polygon enthält
        int fromX = (int) Math.floor(poly.outRect.smallX * map.groundTex.length);
        int toX = (int) Math.ceil(poly.outRect.largeX * map.groundTex.length);
        int fromY = (int) Math.floor(poly.outRect.smallY * map.groundTex[0].length);
        int toY = (int) Math.ceil(poly.outRect.largeY * map.groundTex[0].length);
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                // Genau prüfen, ob enthalten:
                if (poly.contains(1.0 * x / map.groundTex.length, 1.0 * y / map.groundTex[0].length)) {
                    // Position ist im Polygon gültig.
                    // Prüfen, ob auf der gerasterten Map auch genug Platz für eine Einheit der Größe 2 ist.
                    if (positionFree(x, y) && positionFree(x - 1, y) && positionFree(x, y - 1) && positionFree(x - 1, y - 1)) {
                        spawnPositions.add(new Vector(x, y));
                    }
                }
            }
        }
        return spawnPositions;
    }

    /**
     * Findet heraus, ob das gegebenen Feld frei ist.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return true, wenn frei
     */
    private boolean positionFree(int x, int y) {
        if (x >= 0 && x < map.groundTex.length && y >= 0 && y < map.groundTex[0].length) {
            return !map.collision[x][y];
        }
        return false;
    }
}
