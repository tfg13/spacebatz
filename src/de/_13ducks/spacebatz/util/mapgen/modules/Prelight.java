package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.server.gamelogic.ShadowManager;
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
        return new String[]{"LIGHT"};
    }

    @Override
    public boolean computesPolygons() {
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{"SPAWN"};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        map.shadow = new byte[map.groundTex.length][map.groundTex[0].length];
        for (int x = 0; x < map.groundTex.length; x++) {
            for (int y = 0; y < map.groundTex[0].length; y++) {
                if ("surface".equals(parameters.get("type"))) {
                    // Planetenoberfläche (Alle Freiflächen sichtbar)
                    if (map.groundTex[x][y] != 3) {
                        map.shadow[x][y] = 127;
                    }
                } else {
                    // Sonstige: Alles Schwarz
                    map.shadow[x][y] = 127;
                }
            }
        }

        // Eine Runde mit aktiver Beleuchtung vom Spawn aus
        int[] spawn = (int[]) map.metadata.get("SPAWN");
        if ("surface".equals(parameters.get("type"))) {
            // Planetenoberfläche (unendliche Sichtweite)
            ShadowManager.lightShadows(spawn[0], spawn[1], Math.max(map.groundTex.length - spawn[0], spawn[0]) + 1, Math.max(map.groundTex[0].length - spawn[1], spawn[1]) + 1, (byte) 0, (byte) 0, (byte) 32, 0, map.shadow, map.topTex);
        } else {
            // Sonstige: Höhlensichtweite
            ShadowManager.lightShadows(spawn[0], spawn[1], 20, 20, (byte) 0, (byte) 16, (byte) 32, 12, map.shadow, map.topTex);
        }
    }
}
