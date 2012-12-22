package de._13ducks.spacebatz.util.mapgen.data;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * Gruppe von Polygonen.
 * Muss weder rechteckig noch konvex sein
 *
 * Powered by CoR 2 (!)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class PolyMesh {

    public ArrayList<MPolygon> polys = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();

    public static PolyMesh createFromJTSPolygons(GeometryCollection col) {
        PolyMesh mesh = new PolyMesh();
        // Polys erstellen, Nachbarschaftsbeziehungen korrekt eintragen!
        for (int i = 0; i < col.getNumGeometries(); i++) {
            Polygon cPoly = (Polygon) col.getGeometryN(i);
            Coordinate[] coords = cPoly.getCoordinates();
            Node[] polyNodes = new Node[coords.length];
            // Schauen, ob die Nodes schon exisiteren, sonst neue nehmen
            for (int j = 0; j < polyNodes.length; j++) {
                Coordinate coord = coords[j];
                if (coord.x < 0) {
                    coord.x = 0;
                } else if (coord.x > 1) {
                    coord.x = 1;
                }
                if (coord.y < 0) {
                    coord.y = 0;
                } else if (coord.y > 1) {
                    coord.y = 1;
                }
                polyNodes[j] = mesh.getKnownOrNew(coord.x, coord.y);
            }
            MPolygon myPolygon = new MPolygon(true, polyNodes);
            mesh.addPoly(myPolygon);
        }
        return mesh;
    }

    /**
     * Schaut nach, ob der Knoten bereits bekannt ist. Wenn nicht, wird ein neuer angelegt.
     *
     * @param x Die X-Koordinate des zu suchenden Knoten
     * @param y Die Y-Koordinate des zu suchenden Knoten
     * @return Der neue Knoten, entweder aus der Datenbank oder ein ganz neuer
     */
    private Node getKnownOrNew(double x, double y) {
        int index = nodes.indexOf(new Node(x, y));
        Node newnode;
        if (index != -1) {
            newnode = nodes.get(index);
        } else {
            newnode = new Node(x, y);
        }
        nodes.add(newnode);
        return newnode;
    }

    /**
     * Verwaltet Nachbarschaftsbeziehungen und fügt neue Polygone hinzu
     *
     * @param poly ein neuer Polygon
     */
    private void addPoly(MPolygon poly) {
        // Nachbarn suchen
        for (MPolygon freePoly : polys) {
            // Als Nachbar eintragen?
            if (freePoly.isNeighbor(poly)) {
                freePoly.registerNeighbor(poly);
                poly.registerNeighbor(freePoly);
            }
        }
        // Jetzt adden
        polys.add(poly);
    }

    /**
     * Sucht einen Polygon im Mesh, der den gegebene Punkt enthält
     * Kann rekursiv in Unternetzen suchen
     *
     * @param x Koordinate X
     * @param y Koordinate Y
     * @param recursive wenn true, wird rekursiv nach unten weiter gesucht
     * @return Der Polygon, oder null, wenn nicht gefunden
     */
    public MPolygon polyFor(double x, double y, boolean recursive) {
        for (MPolygon po : polys) {
            if (po.contains(x, y)) {
                // Dieser Polygon ist es, aber es könnte rekursiv runter gehen!
                if (po.getMesh() == null || !recursive) {
                    return po;
                } else {
                    return po.getMesh().polyFor(x, y, true);
                }
            }
        }
        return null;
    }
}
