package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
import de._13ducks.spacebatz.util.mapgen.data.Vector;
import java.util.HashMap;

/**
 * Der Default-Rasterizer
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Rasterizer extends Module {

    @Override
    public String getName() {
        return "rasterizer";
    }

    @Override
    public boolean requiresSeed() {
        return false;
    }

    @Override
    public String[] provides() {
        return new String[]{"RASTERIZE"};
    }

    @Override
    public boolean computesPolygons() {
        throw new UnsupportedOperationException("Must not be called.");
    }

    @Override
    public String[] requires() {
        // CREATE_POLY is implizit
        return new String[]{"SPAWN"};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        // Parameter lesen
        int sizeX = Integer.parseInt(parameters.get("sizex"));
        int sizeY = Integer.parseInt(parameters.get("sizey"));
        double scaleX = 1.0 / sizeX;
        double scaleY = 1.0 / sizeY;
        map.groundTex = new int[sizeX][sizeY];
        map.collision = new boolean[sizeX][sizeY];
        MPolygon spawnPoly = null; // Wird paralell noch gesucht.
        // Trivialer Rasterize-Algorithmus. Es gibt bessere - siehe Wikipedia
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                // Suche Polygon
                MPolygon poly = null;
                for (MPolygon po: map.polygons.polys) {
                    if (po.spawn) {
                        spawnPoly = po;
                    }
                    if (po.contains(x * scaleX, y * scaleY)) {
                        poly = po;
                        break;
                    }
                }
                // Textur/Col setzen
                if (poly == null) {
                    map.collision[x][y] = true;
                    continue;
                }
                // Hier kommen wir nur hin, wenn der Polygon gefunden wurde
                if (poly.border) {
                    map.groundTex[x][y] = 1;
                    map.collision[x][y] = true;
                } else if (poly.solid) {
                    map.groundTex[x][y] = 2;
                    map.collision[x][y] = true;
                } else {
                    if (poly.spawn || poly.texture == 0) {
                        map.groundTex[x][y] = 4;
                    } else {
                        // Angeforderte Textur setzen
                        map.groundTex[x][y] = poly.texture;
                    }
                }
            }
        }
        // Undurchdringbaren Rand erzwingen:
        for (int y = 0; y < sizeY; y++) {
            map.groundTex[0][y] = 1;
            map.collision[0][y] = true;
            map.groundTex[sizeX - 1][y] = 1;
            map.collision[sizeX - 1][y] = true;
        }
        for (int x = 0; x < sizeX; x++) {
            map.groundTex[x][0] = 1;
            map.collision[x][0] = true;
            map.groundTex[x][sizeY - 1] = 1;
            map.collision[x][sizeY - 1] = true;
        }
        // Spawn setzen
        Vector spawn = spawnPoly.calcCenter();
        map.metadata.put("SPAWN", new int[]{(int) (spawn.x * sizeX), (int) (spawn.y * sizeY)});
        map.groundTex[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY)] = 6;
        map.groundTex[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY)] = 6;
        map.groundTex[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY) - 1] = 6;
        map.groundTex[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY) - 1] = 6;
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY) - 1] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY) - 1] = false;
    }
}
