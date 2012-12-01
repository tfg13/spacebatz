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
     * Die Größe des Feldes das frei sein muss, dass eine Node als frei gilt.
     * 0 = 1x1, 1=2x2, ...
     */
    private int size;

    /**
     * Initialisiert eine neue NodeFactory.
     */
    public NodeFactory(int size) {
        nodeMap = new Node[Server.game.getLevel().getSizeX()][Server.game.getLevel().getSizeY()];
        this.size = size - 1;
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

        int numNeighbors = 8;
        boolean top = true, bot = true, left = true, right = true, topLeft = true, topRight = true, botLeft = true, botRight = true;




        int i = 0;
        for (int x = posX - 1; x <= posX + 1; x++) {
            for (int y = posY - 1; y <= posY + 1; y++) {

                if ((x != posX || y != posY)) {
                    // Soviele Felder prüfen, wie die Entity dick ist:
                    boolean blocked = false;

                    int startColX = -1, endColX = -1, startColY = -1, endColY = -1;

                    // links unten
                    if (x < posX && y < posY) {
                        startColX = posX - 1;
                        endColX = posX + size;
                        startColY = posY - 1;
                        endColY = posY + size;

                    }

                    // unten
                    if (x == posX && y < posY) {
                        startColX = posX;
                        endColX = posX + size;
                        startColY = posY - 1;
                        endColY = posY - 1;
                    }

                    // unten rechts
                    if (x > posX && y < posY) {
                        startColX = posX;
                        endColX = posX + size + 1;
                        startColY = posY - 1;
                        endColY = posY + size;
                    }

                    // rechts
                    if (x > posX && y == posY) {
                        startColX = posX;
                        endColX = posX + size + 1;
                        startColY = posY;
                        endColY = posY + size;
                    }

                    // rechts oben
                    if (x > posX && y > posY) {
                        startColX = posX;
                        endColX = posX + size + 1;
                        startColY = posY;
                        endColY = posY + size + 1;
                    }

                    // oben
                    if (x == posX && y > posY) {
                        startColX = posX;
                        endColX = posX + size;
                        startColY = posY;
                        endColY = posY + size + 1;
                    }

                    // oben links
                    if (x < posX && y > posY) {
                        startColX = posX - 1;
                        endColX = posX + size;
                        startColY = posY;
                        endColY = posY + size + 1;
                    }

                    // links
                    if (x < posX && y == posY) {
                        startColX = posX - 1;
                        endColX = posX + size - 1;
                        startColY = posY;
                        endColY = posY + size;
                    }

                    for (int colX = startColX; colX <= endColX; colX++) {
                        for (int colY = startColY; colY <= endColY; colY++) {
                            if (0 < colX && colX < Server.game.getLevel().getSizeX() && 0 < colY && colY < Server.game.getLevel().getSizeY() && Server.game.getLevel().getCollisionMap()[colX][colY]) {
                                blocked = true;
                            }
                        }
                    }
                    // Wenn kein Feld blockiert ist:
                    if (!blocked) {
                        neighbors[i] = getNode(x, y);
                        i++;

                    }
                }
            }
        }
        Node newNeighbors[] = new Node[i];
        System.arraycopy(neighbors, 0, newNeighbors, 0, i);
        return newNeighbors;
    }
}
