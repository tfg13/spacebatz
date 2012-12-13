/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
import de._13ducks.spacebatz.util.mapgen.data.Vector;
import java.util.HashMap;
import toxi.math.noise.PerlinNoise;

/**
 * Erstellt Berge/Freiflächen mithilfe von Perlin-Noise
 *
 * @author tfg
 */
public class PerlinTerrainFormer extends Module {

    @Override
    public String getName() {
        return "perlinterrain";
    }

    @Override
    public boolean requiresSeed() {
        return true;
    }

    @Override
    public String[] provides() {
        return new String[]{};
    }

    @Override
    public boolean computesPolygons() {
        return true;
    }

    @Override
    public String[] requires() {
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        PerlinNoise noise = new PerlinNoise();
        noise.noiseSeed(Long.parseLong(parameters.get("SEED")));
        int sizeX = Integer.parseInt(parameters.get("sizex"));
        int sizeY = Integer.parseInt(parameters.get("sizey"));
        float[][] pnoise = new float[sizeX][sizeY];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                pnoise[x][y] = noise.noise(x, y);
            }
        }
        for (MPolygon poly : map.polygons.polys) {
            if (poly.border) {
                continue;
            }
            Vector center = poly.calcCenter();
            if (pnoise[(int) (center.x * sizeX)][(int) (center.y * sizeY)] > 0.5) {
                poly.solid = true;
            }
        }
    }
}
