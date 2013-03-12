package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.geo.Distance;
import de._13ducks.spacebatz.util.geo.IntVector;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Ein Pfad der berechnet wird.
 *
 * @author michael
 */
class AStarImplementation {

    /**
     * Die Liste der Knoten, die noch untersucht werden müssen.
     */
    private PriorityQueue<AStarNode> openList;
    /**
     * Enthält die gleichen Elemente wie die openList, kann aber schneller herausfinden, ob ein bestimmtes enthalten ist.
     */
    private LinkedHashSet<AStarNode> containOpenList;
    /**
     * Die Liste der Knoten die Sackgassen oder Umwege sind.
     */
    private LinkedHashSet<AStarNode> closedList;
    /**
     * Der Startpunkt des Pfads.
     */
    private AStarNode start;
    /**
     * Das Ziel des Pfads.
     */
    private AStarNode goal;
    /**
     * Derjenige, der den Weg bekommt wenn er fertig berechnet ist.
     */
    private PathRequester requester;
    /**
     * Gibt an, ob der aktuelle weg schon fertig berechnet ist.
     */
    private boolean computing;
    /**
     * Der berechnete Pfad.
     */
    private AStarNode[] path;
    /**
     * Die Nodefactory die die Wegknoten erzeugt und verwaltet.
     * Muss für jede Wegberechnung neu erzeugt werden.
     */
    private NodeFactory factory;
    /**
     * Vergleicht 2 Nodes ob sie identisch sind.
     */
    private static Comparator<AStarNode> comparator = new Comparator<AStarNode>() {
        @Override
        public int compare(AStarNode t, AStarNode t1) {
            if (t.fValue < t1.fValue) {
                return -1;
            } else if (t.fValue > t1.fValue) {
                return 1;
            } else {
                return 0;
            }
        }
    };

    /**
     * Initialisiert die AStar-Implemenierung.
     */
    public AStarImplementation() {
        computing = false;
    }

    /**
     * Lädt Start und Ziel einer Wegberechnung, so das mit computeIteration() an dieser gearbeitet werden kann.
     *
     * @param start
     * @param goal
     * @param requester
     */
    public void loadPathRequest(IntVector start, IntVector goal, PathRequester requester, int size) {
        if (computing) {
            throw new IllegalStateException("Der alte Weg ist nocht nicht fertoig!");
        }
        computing = true;
        path = new AStarNode[0];
        factory = new NodeFactory(size);
        closedList = new LinkedHashSet<>();
        openList = new PriorityQueue<>(50, comparator);
        containOpenList = new LinkedHashSet<>(50);
        this.requester = requester;

        this.goal = factory.getNode(goal.x, goal.y);
        this.start = factory.getNode(start.x, start.y);
        openList.add(this.start);
        containOpenList.add(this.start);
    }

    /**
     * Berechnet eine Iteration der Wegfindung.
     * In O(1), d.h. Das braucht nur wenig Rechenzeit.
     * Falls der Weg fertig berechnet wird, wird der Anforderer automatisch benachrichtigt.
     */
    public void computeIteration() {
        if (!computing) {
            throw new IllegalStateException("Kein Weg geladen, habe nichts zum Berechnen!");
        }
        if (!openList.isEmpty()) {
            AStarNode current = openList.poll();
            containOpenList.remove(current);
            if (current.equals(goal)) {
                buildPath(goal);
            } else {
                expandNode(current);
                closedList.add(current);
            }
        } else {
            requester.pathComputed(null);
        }
    }

    /**
     * Baut den Pfad auf, in dem von jeder Node der Vorgänger geholt wird bis man wieder sam Startknoten ist.
     *
     * @param goal der gefundene ZielKnoten.
     */
    private void buildPath(AStarNode goal) {
        ArrayList<AStarNode> nodes = new ArrayList<>();
        AStarNode current = goal;
        while (!current.equals(start)) {
            nodes.add(current);
            current = current.predecessor;
        }
        nodes.add(start);
        path = new AStarNode[nodes.size()];
        int i = 0;
        for (AStarNode n : nodes) {
            path[path.length - 1 - i] = n;
            i++;
        }
        computing = false;
    }

    /**
     * Expandiert einen Knoten.
     *
     * @param current
     */
    private void expandNode(AStarNode current) {
        LinkedList<AStarNode> neighbors = factory.getNeighbors(current);
        for (AStarNode successor: neighbors) {
            if (closedList.contains(successor)) {
                continue;
            }
            float weight = current.wayLength + getWeight(current, successor);
            // Wenn der successor bereits auf der openlost ist, aber der Weg zu ihm nicht kürzer ist abbrechen:
            if (containOpenList.contains(successor) && weight >= successor.wayLength) {
                continue;
            }
            successor.predecessor = current;
            successor.wayLength = weight;

            float cost = current.wayLength + getWeight(successor, goal);
            successor.fValue = cost;
            // Neu einfügen, damit die Sortierung stimmt
            openList.remove(successor);
            openList.add(successor);

        }
    }

    /**
     * Berechnet das Gewicht der Kante zwischen den beiden Knoten.
     *
     * @param current
     * @param successor
     * @return
     */
    private static float getWeight(AStarNode current, AStarNode successor) {
        return (float) Distance.getDistance(current.x, current.y, successor.x, successor.y);
    }

    /**
     * Gibt an ob der aktuell geladene Weg schon berechnet ist.
     *
     * @return the computed
     */
    public boolean isComputed() {
        return !computing;
    }

    /**
     * Bricht die Berechnung ab.
     */
    public void abort() {
        computing = false;
    }

    /**
     * Gibt den Pfad zurück oder einen leeren Pfad wenn die Berechnung abgebrochen wurde.
     * Gibt den Pfad zurück oder einen leeren Pfad null wenn die Berechnung abgebrochen wurde.
     *
     * @return
     */
    public IntVector[] getPath() {
        if (computing) {
            throw new IllegalStateException("Pfad noch nicht fertig berechnet!");
        } else {
            return path;
        }
    }
}
