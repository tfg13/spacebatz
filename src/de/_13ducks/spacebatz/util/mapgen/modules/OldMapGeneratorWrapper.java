package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Bindet den alten MapGenerator als alles erledigendes Super-Modul ein.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OldMapGeneratorWrapper extends Module {

    @Override
    public String getName() {
        return "oldmapgen";
    }

    @Override
    public String[] provides() {
        return new String[]{"CREATE_RASTER"};
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
