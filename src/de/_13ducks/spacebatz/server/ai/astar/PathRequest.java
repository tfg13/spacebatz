package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Position;

/**
 * Eine Anforderung für eine Wegberechnung.
 *
 * @author michael
 */
class PathRequest {

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

    /**
     * Erzeugt ein neues Pathrequest.
     *
     * @param start
     * @param target
     * @param requester
     */
    PathRequest(Position start, Position target, PathRequester requester, int size, AStarImplementation astar) {
        this.start = start;
        this.goal = target;
        this.requester = requester;
        this.requesterSize = size;
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
        requester.pathComputed(new Position[0]);
    }

    void computeIteration() {
        if (computed) {
            throw new IllegalArgumentException("Request ist schon fertig berechnet!");
        }
        aStar.computeIteration();
        if (aStar.isComputed()) {
            requester.pathComputed(aStar.getPath());
            computed = true;
        }
    }

    public int getAge() {
        return Server.game.getTick() - creationTick;
    }
}
