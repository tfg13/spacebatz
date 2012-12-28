package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Berechnet die Anfangs-Schatten.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Prelight extends Module {

    @Override
    public String getName() {
        return "prelight";
    }

    @Override
    public boolean requiresSeed() {
        return false;
    }

    @Override
    public String[] provides() {
        return new String[]{"light"};
    }

    @Override
    public boolean computesPolygons() {
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        map.shadow = new byte[map.groundTex.length][map.groundTex[0].length];
        for (int x = 0; x < map.groundTex.length; x++) {
            for (int y = 0; y < map.groundTex[0].length; y++) {
                // Planetenoberfläche: Nur Berge verdeckt
                if (map.groundTex[x][y] != 3) {
                    map.shadow[x][y] = 127;
                }
            }
        }
    }
}
