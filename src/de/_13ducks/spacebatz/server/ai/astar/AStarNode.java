package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.geo.IntVector;

/**
 * Ein Knoten des Wegfindungsnetzes.
 * Im Grunde eine Position mit zusätzlich einer Variable für das Kantengewicht.
 *
 * @author michael
 */
class AStarNode extends IntVector {

    /**
     * Die Länge des Wegs zu diesem Knoten.
     */
    public float wayLength;
    /**
     * der f-Wert gibt an wie vielversprechend ein Knoten ist.
     * Er berechnet sich durch Strcke zu diesem Knoten + Luftlinie zum Ziel.
     */
    public float fValue;
    /**
     * Der Knoten, von dem aus dieser Erreicht wurde.
     */
    public AStarNode predecessor;

    /**
     * Erzeugt eine neue Node.
     * @param x
     * @param y
     * @param factory 
     */
    public AStarNode(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof AStarNode && ((AStarNode) obj).x == x && ((AStarNode) obj).y == y);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
