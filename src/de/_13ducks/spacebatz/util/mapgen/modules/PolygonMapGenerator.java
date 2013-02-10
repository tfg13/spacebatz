package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.Node;
import de._13ducks.spacebatz.util.mapgen.util.MPolygonSubdivider;
import java.util.HashMap;

/**
 * Generiert eine Polygon-Map
 * Erzeugt dafür eine Zufällige Punktwolke und berechnet anschließend das Voronoi-Diagramm.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class PolygonMapGenerator extends Module {

    @Override
    public String getName() {
        return "polymapgen";
    }

    @Override
    public boolean requiresSeed() {
        return true;
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
        // Parameter auslesen:
        int number = Integer.parseInt(parameters.get("polynumber"));
        long seed = Long.parseLong(parameters.get("SEED"));
        int runs = 0;
        if (parameters.containsKey("smooth")) {
            runs = Integer.parseInt(parameters.get("smooth"));
        }
        map.polygons = MPolygonSubdivider.createMesh(new MPolygon(false, new Node(0,0), new Node(1,0), new Node(1,1), new Node(0,1)), number, runs, seed);
    }
}
