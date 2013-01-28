package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.astar.PrecisePosition;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 * Calculates a path and follows that path untill the target is in direct sightline.
 * Recalculates the path regularly.
 *
 * @author michael
 */
public abstract class GenericFollowOnPathBehaviour extends Behaviour {

    /**
     * The maximal age of the path in gameticks before it is recalculated.
     */
    private int maxPathAge;
    private Player target;
    /**
     * The time of creation of the path we are currently following.
     * -1 if there is no path.
     */
    private int pathCreationTime = -1;

    public GenericFollowOnPathBehaviour(Enemy enemy, Player target, int maxPathAge) {
        super(enemy);
        this.maxPathAge = maxPathAge;
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return targetInSight(owner, target);
        } else if ((Server.game.getTick() - pathCreationTime) > maxPathAge) {
            Server.game.pathfinder.requestPath(new PrecisePosition(owner.getX(), owner.getY()), new PrecisePosition(target.getX(), target.getY()), owner, owner.getSize());
            return this;
        } else {
            return this;
        }
    }

    public abstract Behaviour targetInSight(Enemy owner, Player target);

    @Override
    public Behaviour pathComputed(PrecisePosition[] path) {
        owner.followPath(path);
        return this;
    }
}
