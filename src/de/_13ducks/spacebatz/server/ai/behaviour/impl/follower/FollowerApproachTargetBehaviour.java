package de._13ducks.spacebatz.server.ai.behaviour.impl.follower;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class FollowerApproachTargetBehaviour extends GenericIndirectPursuitBehaviour {

    public FollowerApproachTargetBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        return new FollowerAproachDirectBehaviour(owner, target);
    }

    @Override
    public Behaviour targetLost() {
        return new FollowerLurkBehaviour(owner);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new FollowerLurkBehaviour(owner);
    }
}
