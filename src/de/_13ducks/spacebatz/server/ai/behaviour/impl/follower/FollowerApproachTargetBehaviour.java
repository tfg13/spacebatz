package de._13ducks.spacebatz.server.ai.behaviour.impl.follower;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericApproachTargetBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class FollowerApproachTargetBehaviour extends GenericApproachTargetBehaviour {

    public FollowerApproachTargetBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        owner.stopMovement();
        return new FollowerWatchBehaviour(owner, target);
    }
}
