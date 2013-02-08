package de._13ducks.spacebatz.server.ai.behaviour.impl.follower;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericFrontalAttackBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class FollowerAproachDirectBehaviour extends GenericFrontalAttackBehaviour {

    public FollowerAproachDirectBehaviour(Enemy owner, Player target) {
        super(owner, target, 2);
    }

    @Override
    public Behaviour lostSight(Player target, Enemy owner) {
        return new FollowerApproachTargetBehaviour(owner, target);
    }

    @Override
    public Behaviour reachedDesiredDistance(Player target, Enemy owner) {
        owner.stopMovement();
        return new FollowerWatchBehaviour(owner, target);
    }
}
