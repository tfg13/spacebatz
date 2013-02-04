package de._13ducks.spacebatz.server.ai.behaviour.impl.follower;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class FollowerWatchBehaviour extends Behaviour {

    private Player target;

    public FollowerWatchBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
            if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return this;
        } else {
            return new FollowerApproachTargetBehaviour(owner, target);
        }
    }
}
