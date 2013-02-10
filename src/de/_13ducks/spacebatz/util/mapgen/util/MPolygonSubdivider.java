package de._13ducks.spacebatz.util.mapgen.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import de._13ducks.spacebatz.util.geo.Edge;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.PolyMesh;
import de._13ducks.spacebatz.util.geo.Rect;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Kann gültige Sub-Meshs für bestehende MPolygons berechnen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MPolygonSubdivider {

    /**
     * Erzeugt ein gültiges SubMesh.
     * Dieses verwendet auf jeden Fall die Randknoten des vorhandenen Polygons.
     * Kopiert alle vorhandenen Attribute in die Kinder
     *
     * @param border der Randpolyon
     * @param numberOfPolys wieviele Sub-Polys.
     * @param smoothRuns wie oft das smoothing über das Netz laufen soll.
     * @param randomSeed seed für den Random. Im Zweifelsfall System.nanoTime() nehmen.
     * @return ein gültiges PolyMesh
     */
    public static PolyMesh createMesh(MPolygon border, int numberOfPolys, int smoothRuns, long randomSeed) {
        Random random = new Random(randomSeed);
        // Punkte in diesem groben Rahmen erzeugen
        Rect rectBorder = border.outRect;
        double sizeX = rectBorder.largeX - rectBorder.smallX;
        double sizeY = rectBorder.largeY - rectBorder.smallY;
        // Punktwolke erzeugen:
        ArrayList<Coordinate> points = new ArrayList<>();
        while (points.size() < numberOfPolys) {
            Coordinate candidate = new Coordinate(random.nextDouble() * sizeX + rectBorder.smallX, random.nextDouble() * sizeY + rectBorder.smallY);
            // Punkt auf jeden Fall in dem Rand-Rechteck, muss aber im Polygon sein:
            if (border.contains(candidate.x, candidate.y)) {
                points.add(candidate);
            }
        }
        // Polygonnetz mittels Voronoi bauen:
        VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
        GeometryFactory fact = new GeometryFactory();
        builder.setSites(points);
        GeometryCollection voronoi = (GeometryCollection) builder.getDiagram(fact);

        // Voronoi-Netz auf den Border-Poly clippen:
        voronoi = clip(voronoi, border);

        // Falls der optionale smoooth-parameter angegeben ist, noch so viele smoothing-Durchgänge drüber laufen lassen
        for (int i = 0; i < smoothRuns; i++) {
            // Punktwolke neu berechnen, aus Mittelpunkten der Voronoi-Polygone
            // Dann Voronoi neu berechnen
            points = new ArrayList<>(numberOfPolys);
            for (int p = 0; p < voronoi.getNumGeometries(); p++) {
                Polygon poly = (Polygon) voronoi.getGeometryN(p);
                Coordinate point = poly.getCentroid().getCoordinate();
                points.add(point);
            }
            // Man braucht unbedingt einen neuen Builder, der lässt sich anscheinend nicht wiederverwenden.
            VoronoiDiagramBuilder newBuilder = new VoronoiDiagramBuilder();
            newBuilder.setSites(points);
            voronoi = (GeometryCollection) newBuilder.getDiagram(fact);
            voronoi = clip(voronoi, border);
        }

        PolyMesh mesh = PolyMesh.createFromJTSPolygons(voronoi);
        // Attribute in die Kinder kopieren:
        for (MPolygon subPoly : mesh.polys){
            subPoly.border = border.border;
            subPoly.solid = border.solid;
            subPoly.spawn = border.spawn;
            subPoly.texture = border.texture;
        }
        return mesh;
    }

    /**
     * Clipt diese Menge von Polygonen alle mit dem gegebenen Polygon
     *
     * @param input die Eingabemenge
     * @param border die Grenze für die Flächen
     * @return die geclipten Polygone
     */
    private static GeometryCollection clip(GeometryCollection input, MPolygon border) {
        GeometryFactory fact = new GeometryFactory();
        // Border in ein JTS-Polygon umwandeln:
        List<Edge> edges = border.calcEdges();
        Coordinate[] outline = new Coordinate[edges.size() + 1];
        for (int i = 0; i < edges.size(); i++) {
            outline[i] = new Coordinate(edges.get(i).start.x, edges.get(i).start.y);
        }
        outline[edges.size()] = new Coordinate(edges.get(0).start.x, edges.get(0).start.y);
        Polygon clip = new Polygon(new LinearRing(new CoordinateArraySequence(outline), fact), new LinearRing[0], fact);

        Polygon[] polys = new Polygon[input.getNumGeometries()];
        for (int i = 0; i < input.getNumGeometries(); i++) {
            Polygon poly = (Polygon) input.getGeometryN(i);
            polys[i] = (Polygon) poly.intersection(clip);
        }
        return new GeometryCollection(polys, fact);
    }
}
