package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.HashMap;

/**
 * Linkt das Polygonnetz in die Metadaten, damit der MapViewer es anzeigen kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class PolygonVisualizer extends Module {

    @Override
    public String getName() {
        return "polyVisualizer";
    }

    @Override
    public boolean requiresSeed() {
        return false;
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
        map.metadata.put("VIS_POLYS", map.polygons);
    }
}
