package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Position;

/**
 * Ein Knoten des Wegfindungsnetzes.
 * Im Grunde eine Position mit zusätzlich einer Variable für das Kantengewicht.
 *
 * @author michael
 */
class Node extends Position {

    /**
     * Die Länge des Wegs zu diesem Knoten.
     */
    private float wayLength;
    /**
     * der f-Wert gibt an wie vielversprechend ein Knoten ist.
     * Er berechnet sich durch Strcke zu diesem Knoten + Luftlinie zum Ziel.
     */
    private float fValue;
    /**
     * Der Knoten, von dem aus dieser Erreicht wurde.
     */
    private Node predecessor;
    /**
     * Referenz auf die Factory, die diese Node erzeugt hat und die Nodes verwaltet.
     * Wird verwendet, um die Nachbarknoten zu finden.
     */
    private NodeFactory factory;

    /**
     * Erzeugt eine neue Node.
     * @param x
     * @param y
     * @param factory 
     */
    public Node(int x, int y, NodeFactory factory) {
        super(x, y);
        this.factory = factory;

    }

    /**
     * @return the weight
     */
    public float getWayLength() {
        return wayLength;
    }

    /**
     * @param weight the weight to set
     */
    public void setWayLength(float weight) {
        this.wayLength = weight;
    }

    /**
     * Gibt ein Feld mit Nachbarknoten zurück.
     *
     * @return
     */
    public Node[] getNeighbors() {
        Node neighbors[] = new Node[8];
        int i = 0;
        for (int x = getX() - 1; x <= getX() + 1; x++) {
            for (int y = getY() - 1; y <= getY() + 1; y++) {
                if ((x != getX() || y != getY()) && !Server.game.getLevel().getCollisionMap()[x][y]) {
                    neighbors[i] = factory.getNode(x, y);
                    i++;
                }
            }
        }
        Node newNeighbors[] = new Node[i];
        System.arraycopy(neighbors, 0, newNeighbors, 0, i);
        return newNeighbors;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Node && ((Node) obj).getX() == getX() && ((Node) obj).getY() == getY());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    /**
     * @return the predecessor
     */
    public Node getPredecessor() {
        return predecessor;
    }

    /**
     * @param predecessor the predecessor to set
     */
    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @return the goalDistance
     */
    public float getFValue() {
        return fValue;
    }

    /**
     * @param goalDistance the goalDistance to set
     */
    public void setFValue(float goalDistance) {
        this.fValue = goalDistance;
    }
}
