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
        map.topTex = new int[sizeX][sizeY];
        map.dye_ground = new int[sizeX][sizeY];
        map.dye_top = new int[sizeX][sizeY];
        map.collision = new boolean[sizeX][sizeY];
        // Trivialer Rasterize-Algorithmus. Es gibt bessere - siehe Wikipedia
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                MPolygon poly = map.polygons.polyFor((x + 0.5) * scaleX, (y + 0.5) * scaleY, true);
                // Textur/Col setzen
                if (poly == null) {
                    map.collision[x][y] = true;
                    continue;
                }
                // Hier kommen wir nur hin, wenn der Polygon gefunden wurde
                // Boden erstmal immer gleich setzen:
                map.groundTex[x][y] = 1;
                if (poly.border) {
                    map.topTex[x][y] = 1;
                    map.collision[x][y] = true;
                } else if (poly.solid) {
                    if (poly.resource == 1) {
                        map.topTex[x][y] = 4;
                    } else {
                        map.topTex[x][y] = 2;
                    }
                    map.collision[x][y] = true;
                } else {
                    if (poly.spawn) {
                        map.groundTex[x][y] = 2;
                    } else if (poly.texture != 0) {
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
        MPolygon spawnPoly = null;
        for (MPolygon po : map.polygons.polys) {
            if (po.spawn) {
                spawnPoly = po;
                break;
            }
        }
        Vector spawn = spawnPoly.calcCenter();
        map.metadata.put("SPAWN", new int[]{(int) (spawn.x * sizeX), (int) (spawn.y * sizeY)});
        map.groundTex[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY)] = 5;
        map.groundTex[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY)] = 5;
        map.groundTex[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY) - 1] = 5;
        map.groundTex[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY) - 1] = 5;
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY) - 1] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY) - 1] = false;
    }
}
