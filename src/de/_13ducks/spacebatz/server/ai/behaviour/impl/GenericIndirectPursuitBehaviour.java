package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Calculates a path and follows that path untill the target is in direct sightline.
 * Recalculates the path regularly.
 *
 * @author michael
 */
public abstract class GenericIndirectPursuitBehaviour extends Behaviour {

    /**
     * The maximal age of the path in gameticks before it is recalculated.
     */
    private int maxPathAge = 300;
    private Player target;
    /**
     * Die ID des aktuellen PathRequests.
     */
    private int pathRequestId = -1;
    /**
     * The time of creation of the path we are currently following.
     * -1 if there is no path.
     */
    private int pathCreationTime = -1;

    public GenericIndirectPursuitBehaviour(Enemy enemy, Player target) {
        super(enemy);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            if (pathRequestId != -1) {
                Server.game.pathfinder.deletePathRequest(pathRequestId);
            }
            return targetInSight(owner, target);
        } else if ((Server.game.getTick() - pathCreationTime) > getMaxPathAge()) {
            if (pathRequestId != -1) {
                Server.game.pathfinder.deletePathRequest(pathRequestId);
            }
            pathRequestId = Server.game.pathfinder.requestPath(new Vector(owner.getX(), owner.getY()), new Vector(target.getX(), target.getY()), owner, owner.getSize());
            return this;
        } else {
            return this;
        }
    }

    public abstract Behaviour targetInSight(Enemy owner, Player target);

    @Override
    public Behaviour pathComputed(Vector[] path) {
        owner.followPath(path);
        pathRequestId = -1;
        pathCreationTime = Server.game.getTick();
        return this;
    }

    /**
     * @return the maxPathAge
     */
    public int getMaxPathAge() {
        return maxPathAge;
    }

    /**
     * @param maxPathAge the maxPathAge to set
     */
    public void setMaxPathAge(int maxPathAge) {
        this.maxPathAge = maxPathAge;
    }
}
