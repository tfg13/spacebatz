package de._13ducks.spacebatz.util.mapgen.data;

/**
 * Eine Kante aus 2 Polygonen.
 * Wird für Polygon-Merge Berechnungen verwendet.
 * Die Kante hat zwar einen Start und einen Zielpolygon,
 * bei den meisten Berechnungen spielt die Richtung
 * aber keine Rolle. Deshalb ignorieren die meisten Methoden
 * die Richtung, so z.B. die equals Methode.
 *
 * Powered by CoR 2 (!)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Edge {

    /**
     * Der Startknoten dieser Kante.
     * Im Prinzip einfach irgendein Knoten, da die Richtung meistens egal ist.
     */
    public final Node start;
    /**
     * Der Endknoten dieser Kante.
     * Im Prinzip einfach irgendein Knoten, da die Richtung meistens egal ist.
     */
    public final Node end;

    /**
     * Erzeugt eine neue Kante mit den angegebenen Start- und Zielpunkt.
     * In der Regel ist die Reihenfolge egal, Start- und Zielpunkt als vertauschbar.
     *
     * @param start Der erste Knoten der Kante
     * @param end Der zweite Knoten der Kante
     */
    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Edge) {
            Edge e = (Edge) o;
            // Achtung, beide Richtungen beachten!
            return (e.start.equals(this.start) && e.end.equals(this.end)) || (e.start.equals(this.end) && e.end.equals(this.start));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.start != null ? this.start.hashCode() : 0);
        hash = 79 * hash + (this.end != null ? this.end.hashCode() : 0);
        return hash;
    }

    /**
     * Findet heraus, sich diese und die gegebenen Kante schneiden.
     * Das Verhalten für gleiche Kanten ist undefiniert.
     * Sollte der Schnittpunkt genau auf dem Ende liegen, sagt diese Methode true.
     *
     * @param edge die andere Kante
     * @return true, wenn ein Schnittpunkt existiert, der auf beiden Kanten liegt, sonst false.
     */
    public boolean intersectsWithEndsAllowed(Edge edge) {
        return intersectionWithEndsAllowed(edge) != null;
    }

    public Vector intersectionWithEndsAllowed(Edge edge) {
        // Beide Richtungsvektoren berechnen:
        Vector me = new Vector(end.x - start.x, end.y - start.y);
        Vector other = new Vector(edge.end.x - edge.start.x, edge.end.y - edge.start.y);
        // Gibts einen Schnittpunkt?
        Vector intersection = me.intersectionWith(start.toVector(), edge.start.toVector(), other);
        if (intersection != null && intersection.isValid()) {
            // Liegt dieser Schnittpunkt auf beiden Kante?
            if (intersection.x >= Math.min(start.x, end.x) && intersection.x <= Math.max(start.x, end.x) && intersection.y >= Math.min(start.y, end.y) && intersection.y <= Math.max(start.y, end.y)) {
                if (intersection.x >= Math.min(edge.start.x, edge.end.x) && intersection.x <= Math.max(edge.start.x, edge.end.x) && intersection.y >= Math.min(edge.start.y, edge.end.y) && intersection.y <= Math.max(edge.start.y, edge.end.y)) {
                    return intersection;
                }
            }
        }
        return null;
    }

    /**
     * Findet heraus, sich diese und die gegebenen Kante schneiden.
     * Das Verhalten für gleiche Kanten ist undefiniert.
     * Sollte der Schnittpunkt genau auf dem Ende liegen, sagt diese Methode false.
     *
     * @param edge die andere Kante
     * @return true, wenn ein Schnittpunkt existiert, der auf beiden Kanten liegt, sonst false.
     */
    public boolean intersectsWithEndsNotAllowed(Edge edge) {
        return intersectionWithEndsAllowed(edge) != null;
    }

    public Vector intersectionWithEndsNotAllowed(Edge edge) {
        // Beide Richtungsvektoren berechnen:
        Vector me = new Vector(end.x - start.x, end.y - start.y);
        Vector other = new Vector(edge.end.x - edge.start.x, edge.end.y - edge.start.y);
        // Gibts einen Schnittpunkt?
        Vector intersection = me.intersectionWith(start.toVector(), edge.start.toVector(), other);
        if (intersection != null && intersection.isValid()) {
            // Liegt dieser Schnittpunkt auf beiden Kante?
            if (intersection.x >= Math.min(start.x, end.x) && intersection.x <= Math.max(start.x, end.x) && intersection.y >= Math.min(start.y, end.y) && intersection.y <= Math.max(start.y, end.y)) {
                if (intersection.x >= Math.min(edge.start.x, edge.end.x) && intersection.x <= Math.max(edge.start.x, edge.end.x) && intersection.y >= Math.min(edge.start.y, edge.end.y) && intersection.y <= Math.max(edge.start.y, edge.end.y)) {
                    // Liegts genau auf den Ecken?
                    if (intersection.equals(end.toVector()) || intersection.equals(start.toVector()) || intersection.equals(edge.start.toVector()) || intersection.equals(edge.end.toVector())) {
                        return null; // Dann nicht!
                    }
                    return intersection;
                }
            }
        }
        return null;
    }

    /**
     * Findet den Schnittpunkt zwischen dieser und der gegebenen Edge, sofern er existiert.
     * Nimmt dazu unendlich lange Edges an.
     *
     * @param edge Die andere Kante
     * @return Der Schnittpunkt oder null.
     */
    public Vector endlessIntersection(Edge edge) {
        // Beide Richtungsvektoren berechnen:
        Vector me = new Vector(end.x - start.x, end.y - start.y);
        Vector other = new Vector(edge.end.x - edge.start.x, edge.end.y - edge.start.y);
        // Gibts einen Schnittpunkt?
        Vector inter = me.intersectionWith(start.toVector(), edge.start.toVector(), other);
        if (inter.isValid()) {
            return inter;
        }
        return null;
    }

    /**
     * Findet für Punkte, die auf dieser Kante liegen würden, wenn sie unendlich wäre
     * heraus, ob sie auch auf dieser endlich langen Kante liegen.
     *
     * @param onLine Die Position. MUSS (!) auf der unendlich langen Linie liegen
     * @return true, wenn drauf (ecken zählen mit)
     */
    public boolean partOf(Vector onLine) {
        // Liegt dieser Schnittpunkt auf beiden Kante?
        if (onLine.x >= Math.min(start.x, end.x) && onLine.x <= Math.max(start.x, end.x) && onLine.y >= Math.min(start.y, end.y) && onLine.y <= Math.max(start.y, end.y)) {
            return true;
        }
        return false;
    }

    /**
     * Erzeugt einen neuen Knoten, der auf den Mittelpunkt dieser Kante zeigt.
     *
     * @return einen neuen Knoten, der auf den Mittelpunkt dieser Kante zeigt.
     */
    public Node getCenter() {
        Vector vec = new Vector(start.x - end.x, start.y - end.y);
        vec = vec.multiply(0.5);
        vec = vec.add(end.toVector());
        return new Node(vec.x, vec.y);
    }

    @Override
    public String toString() {
        return start + "-->" + end;
    }

    /**
     * Findet heraus, ob der eine Punkt auf der einen, der andere auf der anderen Seite dieser Linie liegen -
     * oder ob beide auf der gleichen Seite sind.
     * Gibt true, wenn die Seiten unterschiedlich sind.
     *
     * @param pos1 Position 1
     * @param pos2 Position
     * @return
     */
    boolean sidesDiffer(Vector pos1, Vector pos2) {
        // Linie ziehen:
        Edge direct = new Edge(pos1.toNode(), pos2.toNode());
        // Schnittpunkt suchen
        return endlessIntersection(direct) != null;
    }

    /**
     * Liefert eine verschobenen Strecke zurück.
     * Diese hat keine Informationen über registrierte Polygone oder ähnliches.
     *
     * @param vec Dieser Vektor wird an Start- und Zielknoten angehängt.
     */
    Edge move(Vector vec) {
        return new Edge(start.toVector().add(vec).toNode(), end.toVector().add(vec).toNode());
    }
}
