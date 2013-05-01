package de._13ducks.spacebatz.util.geo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein verbesserter Polygon, der zusätzliche Daten speichern kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MPolygon {

    private static final long serialVersionUID = 1L;
    /**
     * Ob dieser Polygon zum Rand gehört und daher auf keinen Fall komplett auf "frei" gesetzt werden darf.
     */
    public boolean border = false;
    /**
     * Ob dieser Polygon ein Berg ist, oder Freifläche.
     */
    public boolean solid = false;
    /**
     * Wenn ungleich 0, dann ist es eine Resource.
     * Wird nur für solid beachtet.
     */
    public int resource = 0;
    /**
     * Ist dies der (einzige) Spawn-Polygon?
     * Im spawn-Polygon spawnt der Spieler.
     */
    public boolean spawn = false;
    /**
     * Spezial-Textur angefordert?
     * (Wird derzeit nur für Freiflächen beachtet)
     */
    public int texture;
    /**
     * Was soll in diesem Polygon so spawnen?
     * Hier geht es um Gegner.
     */
    public HashMap<Integer, Integer> spawnInfo;
    /**
     * Eine Liste mit allen Nodes, die auf einer Kante dieses Polygons liegen oder die eine Ecke darstellen.
     */
    private LinkedList<Node> myNodes;
    /**
     * Die bekannten Nachbarn dieses Polygons.
     */
    private List<MPolygon> neighbors;
    /**
     * Polygone können selber PolyMeshs sein, also wieder Unterpolygone haben.
     * Diese sind aber in keiner Weise logisch mit der Ebene darüber verbunden.
     * Es gibt also insbesondere keine Nachbarschafts-Beziehungen.
     */
    private PolyMesh subPolys;
    /**
     * Das umgebende Rechteckt.
     * Wird für extraschnelle contains()-Berechungen vorberechnet.
     */
    public final Rect outRect;

    /**
     * Erzeugt einen neues Vieleck mit den angegebenen Knoten als Eckpunkten.
     * Testet NICHT, ob das Vieleck auch konvex ist (muss es normalerweise sein)
     * Wirft eine Exception, wenn Parameter null sind oder weniger als 3 geliefert werden.
     * Registriert sich NICHT als Nachbar! (auch nicht, wenn registerNodes true ist!)
     *
     * @param registerNodes Ob dieses neue Polygon bei seinen Nodes registriert werden soll.
     * @param nodes beliebig viele Nodes, mindestens 3
     */
    public MPolygon(boolean registerNodes, Node... nodes) {
        if (nodes == null || nodes.length < 3) {
            throw new IllegalArgumentException("At least three nodes requried!");
        }
        myNodes = new LinkedList<>();
        neighbors = new ArrayList<>();
        myNodes.addAll(Arrays.asList(nodes));
        if (registerNodes) {
            registerNodes();
        }
        outRect = calcMinOuterRect();
    }

    /**
     * Liefert einen Temporären Polygon, der die Verbindung dieses mit dem gegebenen Polygon darstellt.
     * Funktioniert nur, wenn beiden beiden Polygone genau eine gemeinsame Kante (aufgespannt von mindestens 2 gemeinsamen Nodes)
     * haben. Die Resultate in Fällen von mehreren gemeinsamen , aber nicht direkt zusammenhängenden Kanten sind undefiniert.
     *
     * @param poly1 Polygon 1
     * @param poly2 Polygon 2
     * @return einen tempörären Polygon, der gemerged ist.
     */
    public static MPolygon getMergedCopy(MPolygon poly1, MPolygon poly2) {
        // Alternativer Algorithmus mit Kanten:
        // Kanten-Netz bauen
        ArrayList<Edge> edges = new ArrayList<>();

        for (int i = 0; i < poly1.myNodes.size(); i++) {
            edges.add(new Edge(poly1.myNodes.get(i), poly1.myNodes.get(i + 1 < poly1.myNodes.size() ? i + 1 : 0)));
        }

        // Bei Adden alle doppelten Kanten löschen
        for (int i = 0; i < poly2.myNodes.size(); i++) {
            Edge edge = (new Edge(poly2.myNodes.get(i), poly2.myNodes.get(i + 1 < poly2.myNodes.size() ? i + 1 : 0)));
            if (edges.contains(edge)) {
                edges.remove(edge);
            } else {
                edges.add(edge);
            }
        }

        // Jetzt nur noch einen neuen "Weg" bauen. Einfach irgendwo anfangen, es ist eindeutig
        Edge current = edges.get(0);
        Node start = current.start;
        ArrayList<Node> path = new ArrayList<>();
        path.add(start);
        while (!start.equals(current.end)) {
            // Suchen
            for (Edge edge : edges) {
                if (edge.start.equals(current.end)) {
                    // Neues Wegsegment
                    current = edge;
                    path.add(current.start);
                }
            }
        }

        // Fertig, einen neuen Polygon draus machen und returnen
        return new MPolygon(false, path.toArray(new Node[0]));
    }

    /**
     * Liefert eine (unveränderbare) Liste mit allen Knoten dieses Polygons
     *
     * @return eine (unveränderbare) Liste mit allen Knoten dieses Polygons
     */
    public List<Node> getNodes() {
        return Collections.unmodifiableList(myNodes);
    }

    /**
     * Registriert den Polygon bei seinen Nodes.
     * Normalerweise macht dies der Konstruktor automatisch (wenn mit true aufgerufen)
     * Sonst kann man es hier nachholen, z.B. wenn man den temporären Polygon behalten möchte.
     */
    public final void registerNodes() {
        for (Node node : myNodes) {
            node.addPolygon(this);
        }
    }

    /**
     * Überprüft, ob der gefragte Polygon ein Nachbar dieses Feldes ist.
     * Erkennt auch Nachbarn, die nicht als Nachbarn registiert sind (z.B. zur Erstellung der Liste, Vergleich mit temporären etc.)
     * Die Nodes müssen aber wissen, dass sie beide Polygone beinhalten!
     * Sucht nach echten Nachbarn mit geteilter Kante, nicht nur übers Ecke.
     *
     * @param poly der zu Untersuchende Polygon
     * @return true, wenn Nachbar, false wenn nicht.
     */
    public boolean isNeighbor(MPolygon poly) {
        if (neighbors.contains(poly)) {
            return true;
        } else {
            // Manuelle Suche
            int number = 0;
            for (Node node : this.myNodes) {
                if (poly.myNodes.contains(node)) {
                    number++;
                }
            }
            // Fertig mit der Suche.
            if (number >= 2) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Returns a List containing all neighbors of this Polygon
     *
     * @return a List containing all neighbors of this Polygon
     */
    public List<MPolygon> getNeighbors() {
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * Registriert einen Polygon als Nachbar, falls noch nicht registriert
     *
     * @param poly der neue Nachbar
     */
    public void registerNeighbor(MPolygon poly) {
        if (!neighbors.contains(poly)) {
            neighbors.add(poly);
        }
    }

    /**
     * Deregistriert einen Polygon als Nachbar, fall er registriert war
     *
     * @param poly der alte Nachbar.
     */
    public void removeNeighbor(MPolygon poly) {
        neighbors.remove(poly);
    }

    @Override
    public String toString() {
        String ret = "Poly: [";
        for (Node node : myNodes) {
            ret += " " + node;
        }
        return ret + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MPolygon) {
            MPolygon p = (MPolygon) o;
            for (Node node : p.getNodes()) {
                if (!myNodes.contains(node)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.myNodes != null ? this.myNodes.hashCode() : 0);
        return hash;
    }

    /**
     * Findet heraus, ob der gegebene Punkt innerhalb dieses Polygons liegt.
     * Wenn der Punkt genau auf einer Kante oder auf einem Knoten liegt, ist das Resultat undefiniert.
     *
     * @param x X
     * @param y Y
     * @return true, wenn innen, sonst false
     */
    public boolean contains(double x, double y) {
        // Aus Performancegründen erst ein Schnelltest mit der Rechteck-Hülle:
        if (x < outRect.smallX || x > outRect.largeX || y < outRect.smallY || y > outRect.largeY) {
            return false;
        }
        // Verwendet die übliche Strahlmethode. Dabei wird ein Strahl vom zu untersuchenden Punkt in eine beliebige Richtung
        // augesandt. Dann werden die Anzahl der Schnittpunkte mit Kanten des Polygons gezählt.
        // Ist diese Anzahl ungerade, so befindet sich der Punkt innerhalb. Sonst außen.

        // Liste mit Kanten:
        ArrayList<Edge> edges = new ArrayList<>();
        for (int i = 0; i < myNodes.size(); i++) {
            edges.add(new Edge(myNodes.get(i), myNodes.get(i + 1 < myNodes.size() ? i + 1 : 0)));
        }

        // Linie bauen:
        Edge edge = new Edge(new Node(x, y), new Node(500000.2, 600000.7));

        int intersections = 0;
        for (int i = 0; i < edges.size(); i++) {
            // Schnitte suchen
            if (edges.get(i).intersectsWithEndsAllowed(edge)) {
                intersections++;
            }
        }

        // Wenn Anzahl ungerade ist es innen.
        return intersections % 2 == 1;
    }

    /**
     * Prüft, ob dieses Polygon konvex oder konkav ist.
     * True heißt konvex.
     *
     * @return true, wenn konvex.
     */
    public boolean isConvex() {
        boolean rechts = false;
        boolean links = false;
        for (int i = 0; i < myNodes.size(); i++) {
            // Die 3 Nodes holen
            Node n1 = myNodes.get(i > 0 ? i - 1 : myNodes.size() - 1);
            Node n2 = myNodes.get(i);
            Node n3 = myNodes.get(i < myNodes.size() - 1 ? i + 1 : 0);
            // Vorüberprüfungen:
            // Sonderfall:
            // Alle in x oder y-Richtung auf einer Linie:
            if ((n1.x == n2.x && n2.x == n3.x) || (n1.y == n2.y && n2.y == n3.y)) {
                continue; // Weder links noch rechts
            }
            // Sonderfall: n1.x == n2.x. Das erzeugt sonst immer "rechts" was zu schlimmen Polygonen führen kann!
            if (n1.x == n2.x) {
                // Nach oben oder unten?
                if (n1.y < n2.y) {
                    // Zeigt nach oben
                    if (n3.x < n1.x) {
                        links |= true;
                    } else if (n3.x > n1.x) {
                        rechts |= true;
                    }
                } else {
                    // Nach unten
                    if (n3.x < n1.x) {
                        rechts |= true;
                    } else if (n3.x > n1.x) {
                        links |= true;
                    }
                }
            }
            // Rechts oder Links abbiegen?
            // XY Richtung suchen:
            double vecX = n2.x - n1.x;
            double vecY = n2.y - n1.y;
            // y = mx + c
            double m = vecY / vecX;
            double c = n2.y - m * n2.x;
            double checkY = n3.x * m + c;
            if (checkY > n3.y) {
                // Drüber
                if (vecX >= 0) {
                    links |= true;
                } else {
                    rechts |= true;
                }
            } else {
                // Drunter
                if (vecX >= 0) {
                    rechts |= true;
                } else {
                    links |= true;
                }
            }
        }

        return rechts ^ links;
    }

    /**
     * Liefert einen zu dieser Position möglichst nahe liegenden Knoten dieses Polygons.
     *
     * @param pos die Position
     * @return der nächste Knoten
     */
    public Node closestNode(Vector pos) {
        Node nearest = null;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < myNodes.size(); i++) {
            Node n = myNodes.get(i);
            double newdist = Math.sqrt((pos.x - n.x) * (pos.x - n.x) + ((pos.y - n.y) * (pos.y - n.y)));
            if (newdist < dist) {
                dist = newdist;
                nearest = n;
            }
        }
        return nearest;
    }

    public List<Edge> calcEdges() {
        LinkedList<Edge> list = new LinkedList<>();
        for (int i = 0; i < myNodes.size(); i++) {
            list.add(new Edge(myNodes.get(i), myNodes.get(i + 1 < myNodes.size() ? i + 1 : 0)));
        }
        return list;
    }

    /**
     * Liefert das Zentrum dieses Polygons
     * Garantiert nur bei konvexen Polygonen, dass der gelieferte Punkt innerhalb dieses Polygons liegt!
     *
     * @return ein euklidisch berechnetes Centroid (minimaler Manhattan-Abstand zu allen Eckpunkten)
     */
    public Vector calcCenter() {
        double x = 0;
        double y = 0;
        for (Node n : myNodes) {
            x += n.x;
            y += n.y;
        }
        return new Vector(x / myNodes.size(), y / myNodes.size());
    }

    /**
     * Berechnet die Fläche des Polygons.
     * Es muss dazu einfach sein. Ob konvex oder konkav ist aber egal.
     * Verwendet die Gaußsche Trapezformel (siehe Wikipedia).
     *
     * @return die Fläche dieses Polygons
     */
    public double getArea() {
        double area = 0;
        for (int i = 0; i < myNodes.size(); i++) {
            Node node = myNodes.get(i);
            Node next = myNodes.get((i + 1) % myNodes.size()); // + 1 mod elementanzahl
            area += node.x * next.y - next.x * node.y;
        }
        return Math.abs(area / 2);
    }

    /**
     * Liefert das Polygon-Subnetz, falls es existiert.
     *
     * @return das Polygon-Subnetz oder null.
     */
    public PolyMesh getMesh() {
        return subPolys;
    }

    /**
     * Setzt das Polygon-Subnetz.
     * Löscht das alte (falls vorhanden) kommentarlos.
     *
     * @param mesh das neue Polygon-Subnetz.
     */
    public void setMesh(PolyMesh mesh) {
        subPolys = mesh;
    }

    /**
     * Liefert das minimale Rechteck, das den gesamten Polygon enthält.
     *
     * @return das minimale Rechteck, das den gesamten Polygon enthält.
     */
    private Rect calcMinOuterRect() {
        // Einfach größtes und kleinstes X und Y suchen.
        double largeX = Double.MIN_VALUE;
        double smallX = Double.MAX_VALUE;
        double largeY = Double.MIN_VALUE;
        double smallY = Double.MAX_VALUE;
        for (Node node : myNodes) {
            if (node.x > largeX) {
                largeX = node.x;
            }
            if (node.x < smallX) {
                smallX = node.x;
            }
            if (node.y > largeY) {
                largeY = node.y;
            }
            if (node.y < smallY) {
                smallY = node.y;
            }
        }
        // Rechteck daraus bauen
        return new Rect(smallX, largeX, smallY, largeY);
    }
}
