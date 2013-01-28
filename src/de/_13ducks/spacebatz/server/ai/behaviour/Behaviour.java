package de._13ducks.spacebatz.server.ai.behaviour;

import de._13ducks.spacebatz.server.ai.astar.PrecisePosition;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;

/**
 * A Behaviour for an enemy.
 *
 * @author michael
 */
public class Behaviour {

    /**
     * The enemy controlled by this behaviour.
     */
    protected Enemy owner;

    /**
     * Create a new Behaviour.
     *
     * @param owner
     */
    public Behaviour(Enemy owner) {
        this.owner = owner;
    }

    /**
     * Called every tick.
     *
     * @param gameTick
     */
    public Behaviour tick(int gameTick) {
        return this;
    }

    /**
     * Called when a path is computed.
     *
     * @param path
     */
    public Behaviour pathComputed(PrecisePosition[] path) {
        return this;
    }

    /**
     * Called when the target of a setLinearTarget() operation is reached.
     */
    public Behaviour targetReached() {
        return this;
    }

    /**
     * Called when the movement of a setLinearTarget() operation is blocked.
     */
    public Behaviour movementBlocked() {
        return this;
    }

    /**
     * Called when a setLinearTarget() movement is aborted.
     */
    public Behaviour movementAborted() {
        return this;
    }

    /**
     * Called when the Enemy collides with another entity.
     *
     * @param other
     * @return
     */
    public Behaviour onCollision(Entity other) {
        return this;
    }
}
