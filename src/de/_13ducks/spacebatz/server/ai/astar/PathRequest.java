package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.Position;

/**
 * Eine Anforderung für eine Wegberechnung.
 *
 * @author michael
 */
class PathRequest {

    /** Die Startposition */
    private Position start;
    /** Die Zielposition */
    private Position goal;
    /** Der Anforderer, der den gertigen Pfad dann bekommt */
    private PathRequester requester;
    /** Die Breite die der Pfad haben soll. */
    private int requesterSize;
    /** Gibt an zu welchem gameTick das Request erzeugt wurde. */
    private int creationTick;

    /**
     * Erzeugt ein neues Pathrequest.
     *
     * @param start
     * @param target
     * @param requester
     */
    PathRequest(Position start, Position target, PathRequester requester, int size, int gameTick) {
        this.start = start;
        this.goal = target;
        this.requester = requester;
        this.requesterSize = size;
        creationTick = gameTick;
    }

    PathRequester getRequester() {
        return requester;
    }

    /**
     * @return the start
     */
    public Position getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Position start) {
        this.start = start;
    }

    /**
     * @return the goal
     */
    public Position getGoal() {
        return goal;
    }

    /**
     * @param goal the goal to set
     */
    public void setGoal(Position goal) {
        this.goal = goal;
    }

    /**
     * @param requester the requester to set
     */
    public void setRequester(PathRequester requester) {
        this.requester = requester;
    }

    /**
     * @return the requesterSize
     */
    public int getRequesterSize() {
        return requesterSize;
    }

    /**
     * @return the creationTick
     */
    public int getCreationTick() {
        return creationTick;
    }
}
