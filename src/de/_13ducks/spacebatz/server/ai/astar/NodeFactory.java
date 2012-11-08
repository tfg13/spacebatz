package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;

/**
 * Erzeugt und Verwaltet Nodes, so dass bereits erstellte Nodes verwendet werden und nicht immer neue erzeugt werden müssen.
 *
 * @author michael
 */
class NodeFactory {

    /**
     * Die Karte der schon erzeugten Nodes.
     */
    private Node[][] nodeMap;

    /**
     * Initialisiert eine neue NodeFactory.
     */
    public NodeFactory() {
        nodeMap = new Node[Server.game.getLevel().getSizeX()][Server.game.getLevel().getSizeY()];
    }

    /**
     * Gibt die Node der angegebenen Position zurück. Wenn die Node noch nicht existiert wird eine neue angelegt.
     *
     * @param x
     * @param y
     * @return
     */
    public Node getNode(int x, int y) {
        if (nodeMap[x][y] != null) {
            return nodeMap[x][y];
        } else {
            Node node = new Node(x, y, this);
            nodeMap[x][y] = node;
            return node;
        }
    }
}
