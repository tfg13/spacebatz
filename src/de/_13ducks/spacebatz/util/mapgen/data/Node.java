package de._13ducks.spacebatz.util.mapgen.data;

import de._13ducks.spacebatz.Settings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Ein Knoten. Zwischen diesen werden Vielecke aufgespannt (MPolygon)
 * Ein Knoten gehört dabei zu mehreren Polygonen.
 *
 * Powered by CoR 2 (!)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Node {

    /**
     * Diese Listen enthält alle Polygone, auf deren Kanten dieser Polygon liegt oder deren Ecken er markiert.
     */
    private List<MPolygon> myPolys;
    /**
     * Die absolute x-Koordinate dieses Polygons
     */
    public final double x;
    /**
     * Die absolute y-Koordinate dieses Polygons
     */
    public final double y;

    /**
     * Erzeugt einen neuen Knoten mit den angegebenen Koordinaten.
     * Die Koordinaten dürfen nicht negativ sein, sonst gibts ne Exception!
     *
     * @param x Die X-Koordinate
     * @param y Die Y-Koordinate
     */
    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        myPolys = new ArrayList<>();
    }

    /**
     * Registriert einen Polygon bei diesem Knoten.
     * Der Knoten weiß dann in Zunkunft, dass er auf einer der Kanten dieses Polygons liegt (oder diese als Ecke aufspannt)
     * Polygon darf nicht null sein. (IllegalArgumentException)
     * Wenn dieser Node den Polygon bereits kennt passiert nichts.
     *
     * @param poly der neue Polygon
     */
    public void addPolygon(MPolygon poly) {
        if (poly == null) {
            throw new IllegalArgumentException("Poly must not be null!");
        }
        if (!myPolys.contains(poly)) {
            myPolys.add(poly);
        }
    }

    /**
     * Löscht einen registrierten Polygon wieder.
     * Sollte dieser Knoten den Polygon gar nicht kennen, passiert nichts.
     *
     * @param poly der alte Polygon
     */
    public void removePoly(MPolygon poly) {
        myPolys.remove(poly);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node n = (Node) o;
            // Bei Fließkomma-Vergleichen immer eine Toleranz zulassen, wegen den Rundungsfehlern.
            if (Math.abs(n.x - this.x) < 0.01 && Math.abs(n.y - this.y) < Settings.DOUBLE_EQUALS_DIST) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return x + "/" + y;
    }

    /**
     * Liefert eine Vektor-Representation dieses Knotens.
     * Der Vektor enthält nur die derzeitgen Positionsdaten.
     * Alle anderen Daten wie z.B. benachbarte Polygone gehen verloren.
     *
     * @return einen Vektor-Representation dieses Knotens.
     */
    public Vector toVector() {
        return new Vector(x, y);
    }

    public List<Node> getReachableNodes() {
        LinkedList<Node> nodes = new LinkedList<>();
        for (MPolygon poly : myPolys) {
            List<Node> polynodes = poly.getNodes();
            for (Node n : polynodes) {
                if (!nodes.contains(n)) {
                    nodes.add(n);
                }
            }
        }
        // Uns selber raus nehmen, falls drin
        nodes.remove(this);
        return nodes;
    }

    /**
     * Liefert die Kosten (Wegfindung, siehe dort) von diesem Knoten zu einem anderen.
     *
     * @param node der andere
     * @return die Kosten von diesem Knoten zu einem anderen.
     */
    public double movementCostTo(Node node) {
        return Math.sqrt((x - node.x) * (x - node.x) + (y - node.y) * (y - node.y));
    }

    public List<MPolygon> getPolygons() {
        return Collections.unmodifiableList(myPolys);
    }
}
