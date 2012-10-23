package de._13ducks.spacebatz.util.mapgen.modules;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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

        Random random = new Random(seed);

        // Zufalls-Punktwolke erzeugen:
        ArrayList<Coordinate> points = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            points.add(new Coordinate(random.nextDouble(), random.nextDouble()));
        }

        // Voronoi erzeugen
        VoronoiDiagramBuilder vbuilder = new VoronoiDiagramBuilder();
        GeometryFactory geom = new GeometryFactory();

        vbuilder.setSites(points);

        GeometryCollection voronoi = (GeometryCollection) vbuilder.getDiagram(geom);

        map.polygons = voronoi;

        // Falls der optionale smoooth-parameter angegeben ist, noch so viele smoothing-läuft drüber laufen lassen
        if (parameters.containsKey("smooth")) {
            int runs = Integer.parseInt(parameters.get("smooth"));
            for (int i = 0; i < runs; i++) {
                // Punktwolke neu berechnen, aus Mittelpunkten der Voronoi-Polygone
                // Dann Voronoi neu berechnen
                points = new ArrayList<>(number);
                for (int p = 0; p < map.polygons.getNumGeometries(); p++) {
                    Polygon poly = (Polygon) map.polygons.getGeometryN(p);
                    points.add(poly.getCentroid().getCoordinate());
                }
                // Man braucht unbedingt einen neuen Builder, der lässt sich anscheinend nicht wiederverwenden.
                VoronoiDiagramBuilder newBuilder = new VoronoiDiagramBuilder();
                newBuilder.setSites(points);
                map.polygons = (GeometryCollection) newBuilder.getDiagram(geom);
            }
        }
    }
}
