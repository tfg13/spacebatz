package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

/**
 *
 * @author michael
 */
public abstract class GenericWatchTargetBehaviour extends GenericStandDivergeBehaviour {

    private double minDistance;
    private double maxDistance;
    private Player target;

    public GenericWatchTargetBehaviour(Enemy owner, Player target, double minDistance, double maxDistance) {
        super(owner);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.target = target;
        owner.setFacingTarget(target);
    }

    @Override
    public Behaviour tick(int gameTick) {
        Behaviour superReturn = super.tick(gameTick);
        double distance = GeoTools.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY());
        if (!owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return lostSightContact(owner, target);
        } else {
            if (distance < minDistance) {
                return toCloseToTarget(owner, target);
            } else if (distance > maxDistance) {
                return toFarFromTarget(owner, target);
            }
        }
        return superReturn;
    }

    public abstract Behaviour toCloseToTarget(Enemy owner, Player target);

    public abstract Behaviour toFarFromTarget(Enemy owner, Player target);

    public abstract Behaviour lostSightContact(Enemy owner, Player target);
}
