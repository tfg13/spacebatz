package de._13ducks.spacebatz.server.ai.behaviour.impl.shooter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericDirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class ShooterAproachDirectBehaviour extends GenericDirectPursuitBehaviour {

    public ShooterAproachDirectBehaviour(Enemy owner, Player target) {
        super(owner, target, 5);
    }

    @Override
    public Behaviour lostSight(Player target, Enemy owner) {
        return new ShooterApproachIndirectBehaviour(owner, target);
    }

    @Override
    public Behaviour reachedDesiredDistance(Player target, Enemy owner) {
        owner.stopMovement();
        return new ShooterShootBehaviour(owner, target);
    }
}
