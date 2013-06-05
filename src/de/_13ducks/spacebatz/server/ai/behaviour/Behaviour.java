package de._13ducks.spacebatz.server.ai.behaviour;

import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * A Behaviour for an enemy. Behaviours can react to the events given by their
 * owner. Each event function returns a behaviour, whom the next events will be
 * given. By returning a new / other Behaviour than self, the behavioutr of an
 * enemy can be changed.
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
    public Behaviour pathComputed(Vector[] path) {
        return this;
    }

    /**
     * Called when the target of a setLinearTarget() operation is reached.
     */
    public Behaviour getTargetReachedBehaviour() {
        return this;
    }

    /**
     * Called when the movement of a setLinearTarget() operation is blocked.
     */
    public Behaviour getMovementBlockedBehaviour() {
        return this;
    }

    /**
     * Called when a setLinearTarget() movement is aborted.
     */
    public Behaviour getMovementAbortedBehaviour() {
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

    /**
     * Wird aufgerufen wenn das verfolgte Ziel stirbt.
     *
     * @return
     */
    public Behaviour onTargetDeath() {
        return this;
    }

    public Behaviour onAttackTarget(Player target) {
        return this;
    }
}
