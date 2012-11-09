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
            Node node = new Node(x, y);
            nodeMap[x][y] = node;
            return node;
        }
    }

    /**
     * Gibt ein Feld mit Nachbarknoten zurück.
     * Nachbarknoten sind solche, die neben der Node liegen und begehbar sind.
     *
     * @return
     */
    public Node[] getNeighbors(Node node) {
        int posX = node.getX();
        int posY = node.getY();
        Node neighbors[] = new Node[8];
        int i = 0;
        for (int x = posX - 1; x <= posX + 1; x++) {
            for (int y = posY - 1; y <= posY + 1; y++) {
                if ((x != posX || y != posY) && !Server.game.getLevel().getCollisionMap()[x][y]) {
                    neighbors[i] = getNode(x, y);
                    i++;
                }
            }
        }
        Node newNeighbors[] = new Node[i];
        System.arraycopy(neighbors, 0, newNeighbors, 0, i);
        return newNeighbors;
    }
}
