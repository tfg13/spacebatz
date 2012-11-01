package de._13ducks.spacebatz.util.mapgen.modules;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
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
    public boolean requiresSeed() {
        return false;
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
        // Parameter lesen
        int sizeX = Integer.parseInt(parameters.get("sizex"));
        int sizeY = Integer.parseInt(parameters.get("sizey"));
        double scaleX = 1.0 / sizeX;
        double scaleY = 1.0 / sizeY;
        map.groundTex = new int[sizeX][sizeY];
        map.collision = new boolean[sizeX][sizeY];
        GeometryFactory fact = new GeometryFactory();
        // Trivialer Rasterize-Algorithmus. Es gibt bessere - siehe Wikipedia
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Point p = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(x * scaleX, y * scaleY)}), fact);
                // Suche Polygon
                MPolygon poly = null;
                for (int i = 0; i < map.polygons.getNumGeometries(); i++) {
                    if (map.polygons.getGeometryN(i).covers(p)) {
                        poly = (MPolygon) map.polygons.getGeometryN(i);
                        break;
                    }
                }
                // Textur/Col setzen
                if (poly == null) {
                    map.collision[x][y] = true;
                    continue;
                }
                // Hier kommen wir nur hin, wenn der Polygon gefunden wurde
                if (poly.border) {
                    map.groundTex[x][y] = 1;
                    map.collision[x][y] = true;
                }
            }
        }
    }
}
