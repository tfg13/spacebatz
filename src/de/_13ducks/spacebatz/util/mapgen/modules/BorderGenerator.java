package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
import de._13ducks.spacebatz.util.mapgen.data.Node;
import java.util.HashMap;

/**
 * Sucht nach Rand-Polygonen und markiert sie als "border".
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class BorderGenerator extends Module {

    @Override
    public String getName() {
        return "bordergenerator";
    }

    @Override
    public boolean requiresSeed() {
        return false;
    }

    @Override
    public String[] provides() {
        return new String[]{"BORDER"};
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
        for (MPolygon poly : map.polygons.polys) {
            for (Node n : poly.getNodes()) {
                if (n.x <= 0 || n.x >= 1 || n.y <= 0 || n.y >= 1) {
                    poly.border = true;
                    break;
                }
            }
        }
    }
}
