package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
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
        return new String[]{};
    }

    @Override
    public void computeMap(InternalMap map, HashMap<String, String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
