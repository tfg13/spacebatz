package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.Vector;
import de._13ducks.spacebatz.util.mapgen.data.Theme;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Der Default-Rasterizer
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Rasterizer extends Module {

    private HashMap<String, Theme> themes;

    public Rasterizer() {
        readThemes();
    }

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
        Theme theme = themes.get(parameters.get("theme"));
        if (theme == null) {
            throw new RuntimeException("ERROR: Theme " + parameters.get("theme") + " not found!");
        }
        double scaleX = 1.0 / sizeX;
        double scaleY = 1.0 / sizeY;
        map.groundTex = new int[sizeX][sizeY];
        map.topTex = new int[sizeX][sizeY];
        map.ground_randomize = new byte[sizeX][sizeY];
        map.top_randomize = new byte[sizeX][sizeY];
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
                    map.collision[x][y] = true;
                    map.groundTex[x][y] = 32;
                    if (poly.resource == 1) {
                        map.topTex[x][y] = 4;
                    } else {
                        map.topTex[x][y] = 2;
                    }
                } else {
                    if (poly.texture != 0) {
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
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY)] = false;
        map.collision[(int) (spawn.x * sizeX)][(int) (spawn.y * sizeY) - 1] = false;
        map.collision[(int) (spawn.x * sizeX) - 1][(int) (spawn.y * sizeY) - 1] = false;
    }

    /**
     * Liest alle Themes aus map/themes/ ein.
     *
     * @return eine Liste mit allen geladenen Themes.
     */
    private void readThemes() {
        themes = new HashMap<>();
        File themeFolder = new File("map/themes");
        File[] themeFiles = themeFolder.listFiles();
        OUTER:
        for (File themeFile : themeFiles) {
            int ground = 0, belowWall = 0, spawn = 0, border = 0, wall = 0;
            // Versuche es zu parsen
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(themeFile))) {
                   String line;
                   while ((line = reader.readLine()) != null) {
                       String[] lineSplit = line.split("=");
                       int val = Integer.parseInt(lineSplit[1], 16);
                       switch (lineSplit[0]) {
                           case "ground":
                               ground = val;
                               break;
                           case "belowWall":
                               belowWall = val;
                               break;
                           case "spawn":
                               spawn = val;
                               break;
                           case "border":
                               border = val;
                               break;
                           case "wall":
                               wall = val;
                               break;
                           default:
                               System.out.println("Error parsing theme " + themeFile.getName());
                               continue OUTER;
                       }
                   }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            themes.put(themeFile.getName(), new Theme(ground, belowWall, spawn, border, wall));
        }
    }
}
