package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Genertiert eine Polygon-Map
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class PolygonMapGenerator extends Module {

    @Override
    public String getName() {
        return "polymapgen";
    }

    @Override
    public String[] provides() {
        return new String[]{"CREATE_POLY"};
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
