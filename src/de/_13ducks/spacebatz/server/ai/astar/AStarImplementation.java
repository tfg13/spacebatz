package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.Distance;
import de._13ducks.spacebatz.util.Position;
import java.util.ArrayList;
import java.util.Comparator;
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
    private PriorityQueue<Node> openList;
    /**
     * Die Liste der Knoten die Sackgassen oder Umwege sind.
     */
    private ArrayList<Node> closedList;
    /**
     * Der Startpunkt des Pfads.
     */
    private Node start;
    /**
     * Das Ziel des Pfads.
     */
    private Node goal;
    /**
     * Derjenige, der den Weg bekommt wenn er fertig berechnet ist.
     */
    private PathRequester requester;
    /**
     * Gibt an, ob der aktuelle weg schon fertig berechnet ist.
     */
    private boolean computed;
    /**
     * Die Nodefactory die die Wegknoten erzeugt und verwaltet.
     * Muss für jede Wegberechnung neu erzeugt werden.
     */
    private NodeFactory factory;

    /**
     * Initialisiert die AStar-Implemenierung.
     */
    public AStarImplementation() {
        openList = new PriorityQueue(11, new Comparator<Node>() {

            @Override
            public int compare(Node t, Node t1) {
                if (t.getFValue() < t1.getFValue()) {
                    return -1;
                } else if (t.getFValue() > t1.getFValue()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        computed = true;
    }

    /**
     * Lädt Start und Ziel einer Wegberechnung, so das mit computeIteration() an dieser gearbeitet werden kann.
     *
     * @param start
     * @param goal
     * @param requester
     */
    public void loadPathRequest(Position start, Position goal, PathRequester requester) {
        if (!isComputed()) {
            throw new IllegalStateException("Der alte Weg ist nocht nicht fertoig!");
        }
        computed = false;
        factory = new NodeFactory();
        closedList = new ArrayList<>();
        this.requester = requester;

        this.goal = factory.getNode(goal.getX(), goal.getY());
        this.start = factory.getNode(start.getX(), start.getY());
        openList.add(this.start);
    }

    /**
     * Berechnet eine Iteration der Wegfindung.
     * In O(1), d.h. Das braucht nur wenig Rechenzeit.
     * Falls der Weg fertig berechnet wird, wird der Anforderer automatisch benachrichtigt.
     */
    public void computeIteration() {
        if (!openList.isEmpty()) {
            Node current = openList.poll();
            if (current.equals(goal)) {
                buildAndDeliverPath(goal);
            } else {
                expandNode(current);
                closedList.add(current);
            }
        } else {
            throw new IllegalStateException("Kein Weg gefunden!");
        }
    }

    /**
     * Baut den Pfad auf, in dem von jeder Node der Vorgänger geholt wird bis man wieder sam Startknoten ist.
     *
     * @param goal der gefundene ZielKnoten.
     */
    private void buildAndDeliverPath(Node goal) {
        ArrayList<Node> nodes = new ArrayList<>();
        Node current = goal;
        while (!current.equals(start)) {
            nodes.add(current);
            current = current.getPredecessor();
        }
        Node path[] = new Node[nodes.size()];
        int i = 0;
        for (Node n : nodes) {
            path[i] = n;
            i++;
        }
        requester.pathComputed(path);
        computed = true;
    }

    /**
     * Expandiert einen Knoten.
     *
     * @param current
     */
    private void expandNode(Node current) {
        Node neighbors[] = factory.getNeighbors(current);
        for (int i = 0; i < neighbors.length; i++) {
            Node successor = neighbors[i];
            if (closedList.contains(successor)) {
                continue;
            }
            float weight = current.getWayLength() + getWeight(current, successor);
            // Wenn der successor bereits auf der openlost ist, aber der Weg zu ihm nicht kürzer ist abbrechen:
            if (openList.contains(successor) && weight >= successor.getWayLength()) {
                continue;
            }
            successor.setPredecessor(current);
            successor.setWayLength(weight);

            float cost = current.getWayLength() + getWeight(successor, goal);
            successor.setFValue(cost);
            if (openList.contains(successor)) {
                openList.remove(successor);
            }
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
    private float getWeight(Node current, Node successor) {
        return (float) Distance.getDistance(current.getX(), current.getY(), successor.getX(), successor.getY());
    }

    /**
     * Gibt an ob der aktuell geladene Weg schon berechnet ist.
     *
     * @return the computed
     */
    public boolean isComputed() {
        return computed;
    }
}
