package de._13ducks.spacebatz.util.mapgen.modules;

import com.vividsolutions.jts.geom.Coordinate;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
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
        for (int i = 0; i < map.polygons.getNumGeometries(); i++) {
            MPolygon poly = (MPolygon) map.polygons.getGeometryN(i);
            Coordinate[] coords = poly.getCoordinates();
            for (Coordinate c : coords) {
                if (c.x <= 0 || c.x >= 1 || c.y <= 0 || c.y >= 1) {
                    poly.border = true;
                    break;
                }
            }
        }
    }
}
