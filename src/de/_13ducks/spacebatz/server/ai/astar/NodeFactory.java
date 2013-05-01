package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;
import java.util.LinkedList;

/**
 * Erzeugt und Verwaltet Nodes, so dass bereits erstellte Nodes verwendet werden und nicht immer neue erzeugt werden müssen.
 *
 * @author michael
 */
class NodeFactory {

    /**
     * Die Karte der schon erzeugten Nodes.
     */
    private AStarNode[][] nodeMap;
    /**
     * Die Größe des Feldes das frei sein muss, dass eine Node als frei gilt.
     * 0 = 1x1, 1=2x2, ...
     */
    private int size;

    /**
     * Initialisiert eine neue NodeFactory.
     */
    public NodeFactory(int size) {
        nodeMap = new AStarNode[Server.game.getLevel().getSizeX()][Server.game.getLevel().getSizeY()];
        this.size = size - 1;
    }

    /**
     * Gibt die Node der angegebenen Position zurück. Wenn die Node noch nicht existiert wird eine neue angelegt.
     *
     * @param x
     * @param y
     * @return
     */
    public AStarNode getNode(int x, int y) {
        if (nodeMap[x][y] != null) {
            return nodeMap[x][y];
        } else {
            AStarNode node = new AStarNode(x, y);
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
    public LinkedList<AStarNode> getNeighbors(AStarNode node) {
        int posX = node.x;
        int posY = node.y;
        LinkedList<AStarNode> neighbors = new LinkedList<>();


        // Werte cachen:
        int sizeX = Server.game.getLevel().getSizeX();
        int sizeY = Server.game.getLevel().getSizeY();
        boolean[][] collisionMap = Server.game.getLevel().getCollisionMap();

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
                            if (0 < colX && colX < sizeX && 0 < colY && colY < sizeY && collisionMap[colX][colY]) {
                                blocked = true;
                                break;
                            }
                        }
                    }
                    // Wenn kein Feld blockiert ist:
                    if (!blocked && (0 <= x && x <= nodeMap.length) && (0 <= y && y <= nodeMap[0].length)) {
                        neighbors.add(getNode(x, y));

                    }
                }
            }
        }
        return neighbors;
    }
}
