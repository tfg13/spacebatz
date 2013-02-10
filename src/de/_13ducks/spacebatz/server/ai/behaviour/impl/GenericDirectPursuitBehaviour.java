package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Distance;

/**
 * Directly walks towards the target until the desired distance is reached or sightline is interrupted.
 *
 * @author michael
 */
public abstract class GenericDirectPursuitBehaviour extends Behaviour {

    private Player target;
    private double desiredDisntance;

    public GenericDirectPursuitBehaviour(Enemy owner, Player target, int desiredDistance) {
        super(owner);
        this.target = target;
        this.desiredDisntance = desiredDistance;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (!owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return lostSight(target, owner);
        } else if (Distance.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) < desiredDisntance) {
            return reachedDesiredDistance(target, owner);
        } else {
            owner.setVector(target.getX() - owner.getX(), target.getY() - owner.getY());
            return this;
        }
    }

    /**
     * Called when we lost sight of our target.
     *
     * @param target
     * @param owner
     * @return
     */
    public abstract Behaviour lostSight(Player target, Enemy owner);

    /**
     * Called when the desired Distance is reached,
     *
     * @param target
     * @param owner
     * @return
     */
    public abstract Behaviour reachedDesiredDistance(Player target, Enemy owner);
}
