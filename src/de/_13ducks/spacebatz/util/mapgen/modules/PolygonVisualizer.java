package de._13ducks.spacebatz.util.mapgen.modules;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Polygon;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Schreibt das Polygonnetz so in die Metadaten, dass der Mapviewer es anzeigen kann.
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
        ArrayList<Coordinate> points = new ArrayList<>();
        for (int i = 0; i < map.polygons.getNumGeometries(); i++) {
            Polygon poly = (Polygon) map.polygons.getGeometryN(i);
            CoordinateSequence outLine = poly.getExteriorRing().getCoordinateSequence();
            Coordinate previous = outLine.getCoordinate(0);
            for (int j = 1; j < outLine.size(); j++) {
                points.add(previous);
                Coordinate next = outLine.getCoordinate(j);
                points.add(next);
                previous = next;
            }
            // Polygon schließen:
            points.add(previous);
            points.add(outLine.getCoordinate(0));
        }
        map.metadata.put("VIS_POLYS", points);
    }

}
