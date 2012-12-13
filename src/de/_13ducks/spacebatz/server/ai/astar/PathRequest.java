package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Position;

/**
 * Eine Anforderung für eine Wegberechnung.
 *
 * @author michael
 */
public class PathRequest {

    /** Die Startposition. */
    private Position start;
    /** Die Zielposition. */
    private Position goal;
    /** Der Anforderer, der den gertigen Pfad dann bekommt. */
    private PathRequester requester;
    /** Die Breite die der Pfad haben soll. */
    private int requesterSize;
    /** Gibt an zu welchem gameTick das Request erzeugt wurde. */
    private int creationTick;
    /** Gibt an ob dieses Request noch berechnet ist oder fertig/abgebrochen ist. */
    private boolean computed;
    /** Der Astar-Algorithmus der verwendet wird. */
    private AStarImplementation aStar;
//    /** Die Erste Position auf die die Entity laufen muss um am Raster ausgerichtet zu sein. */
//    private PrecisePosition firstPosition;
    /** Die Transformation von Entitykoordinaten zu Kollisionsmapkoordinaten. */
    private double dx, dy;

    /**
     * Erzeugt ein neues Pathrequest.
     *
     * @param start
     * @param target
     * @param requester
     */
    PathRequest(PrecisePosition start, PrecisePosition target, PathRequester requester, double size, AStarImplementation astar) {

        // Das linke untere Feld des Kollisionsrechtecks der Entity berechnen:
        Position leftBotPosition = getLeftBotPosition(start, size);
        int leftBotFieldX = leftBotPosition.getX();
        int leftBotFieldY = leftBotPosition.getY();

        if (Server.game.getLevel().getCollisionMap()[leftBotFieldX][leftBotFieldY]) {
            System.out.println("PathRequest: Invalid LB-Position!");
        }
        // Die Position auf die die Entity gehen muss, das sie in das (am Raster ausgerichtete) Kollisionsrechteck passt:
        double firstPositionX = start.getX() - ((start.getX() + (size / 2)) - (leftBotFieldX + size));
        double firstPositionY = start.getY() - ((start.getY() + (size / 2)) - (leftBotFieldY + size));

        for (int x = (int) (firstPositionX - (size / 2)); x <= (int) (firstPositionX + (size / 2)); x++) {
            for (int y = (int) (firstPositionY - (size / 2)); y <= (int) (firstPositionY + (size / 2)); y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y]) {
                    System.out.println("Invalid LB Position!");
                }
            }
        }

        // Die Transformation zwischen Entitykoordinaten und Pathfinderkoordinate berechnen:
        dx = firstPositionX - leftBotFieldX;
        dy = firstPositionY - leftBotFieldY;

//        // Die erste Position speichern dass sie später an den wEg vorne angefügt werden kann:
//        firstPosition = new PrecisePosition(firstPositionX, firstPositionY);

        // Das linke untere Feld als Startfeld der WEgberechnung setzen:
        this.start = new Position(leftBotFieldX, leftBotFieldY);
        // Linkes unteres Feld der Zielposition bestimmen:
        this.goal = new Position((int) (target.getX() - size / 2), (int) (target.getY() - size / 2));

        // Restliche Wegfindungsinfos setzen:
        this.requester = requester;
        this.requesterSize = (int) (size + 1);
        this.creationTick = Server.game.getTick();
        this.aStar = astar;
    }

    public void initialise() {
        aStar.loadPathRequest(start, goal, requester, requesterSize);
    }

    public boolean isDone() {
        return computed;
    }

    public void abort() {
        computed = true;
        aStar.abort();
        requester.pathComputed(new PrecisePosition[0]);
    }

    void computeIteration() {
        if (computed) {
            throw new IllegalArgumentException("Request ist schon fertig berechnet!");
        }
        aStar.computeIteration();
        if (aStar.isComputed()) {
            Position path[] = aStar.getPath();
            for (int i = 0; i < path.length; i++) {
                Server.game.getLevel().createDestroyableBlock((int) path[i].getX(), (int) path[i].getY(), 10);
            }
            PrecisePosition finalPath[];
            if (path.length == 0) {
                finalPath = new PrecisePosition[0];
            } else {
                // Startposition vorne anhängen und den Weg zu Entitykoordinaten transformieren:
                finalPath = new PrecisePosition[path.length];

//                finalPath[0] = firstPosition;

                for (int i = 0; i < path.length; i++) {
                    finalPath[i] = new PrecisePosition(path[i].getX() + dx, path[i].getY() + dy);
                }
            }


            // Den fertigen Pfad übergeben;
            requester.pathComputed(finalPath);
            computed = true;
        }
    }

    public int getAge() {
        return Server.game.getTick() - creationTick;
    }

    public static Position getLeftBotPosition(PrecisePosition actualPosition, double size) {
        // oben, links, rechts und unten auf kollision prüfen
        boolean topCollision = false, rightCollision = false, botCollision = false, leftCollision = false;


        for (int x = (int) (actualPosition.getX() - size / 2); x <= (int) (actualPosition.getX() + size / 2); x++) {
            if (Server.game.getLevel().getCollisionMap()[x][(int) (actualPosition.getY() + (size / 2))]) {
                topCollision = true;
//                System.out.println("top");
            }
            if (Server.game.getLevel().getCollisionMap()[x][(int) (actualPosition.getY() - (size / 2))]) {
                botCollision = true;
//                System.out.println("bot");
            }
        }

        for (int y = (int) (actualPosition.getY() - size / 2); y <= (int) (actualPosition.getY() + size / 2); y++) {
            if (Server.game.getLevel().getCollisionMap()[(int) (actualPosition.getX() + (size / 2))][y]) {
                rightCollision = true;
//                System.out.println("right");

            }
            if (Server.game.getLevel().getCollisionMap()[(int) (actualPosition.getX() - (size / 2))][y]) {
                leftCollision = true;
//                System.out.println("left");
            }
        }

        // botleft feld normal berechnen:
        int leftBotFieldX = (int) (actualPosition.getX() - (size / 2));
        int leftBotFieldY = (int) (actualPosition.getY() - (size / 2));

//        System.out.println("wrong field: " + leftBotFieldX + " " + leftBotFieldY);
        // botleft feld von den kollisionen weg verschieben (kollision rechts -> nach links verschieben)

        if (topCollision) {
            leftBotFieldY--;
        } else if (rightCollision) {
            leftBotFieldX--;
        } else if (botCollision) {
            leftBotFieldY++;
        } else if (leftCollision) {
            leftBotFieldX++;
        }
        // bei kolliison oben und unten oder links und rechts fehler!

        if ((topCollision && botCollision) || (leftCollision && rightCollision)) {
            throw new IllegalStateException("OMG Entity ist zwischen Wänden eingeklemmt!");
        }





        return new Position(leftBotFieldX, leftBotFieldY);
    }
}
