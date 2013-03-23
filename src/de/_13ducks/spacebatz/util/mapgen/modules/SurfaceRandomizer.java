package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class SurfaceRandomizer extends Module {

    @Override
    public String getName() {
        return "surfacerandomizer";
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
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        int[][] topTex = map.topTex;
        byte[][] topRandom = map.top_randomize;
        Random random = new Random(Long.parseLong(parameters.get("SEED")));
        for (int x = 0; x < topTex.length; x++) {
            for (int y = 0; y < topTex[0].length; y++) {
                if (topTex[x][y] == 1 || topTex[x][y] == 2) {
                    int fact = random.nextInt(3);
                    topRandom[x][y] = (byte) (fact * 16);
                }
            }
        }
    }

}
