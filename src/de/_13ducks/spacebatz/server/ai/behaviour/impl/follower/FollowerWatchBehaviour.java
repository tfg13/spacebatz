package de._13ducks.spacebatz.server.ai.behaviour.impl.follower;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericWatchTargetBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class FollowerWatchBehaviour extends GenericWatchTargetBehaviour {

    public FollowerWatchBehaviour(Enemy owner, Player target) {
        super(owner, target, 0, Double.MAX_VALUE);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new FollowerLurkBehaviour(owner);
    }

    @Override
    public Behaviour toCloseToTarget(Enemy owner, Player target) {
        // Ist uns egal, wenn wir zu nahe dran sind
        return this;
    }

    @Override
    public Behaviour toFarFromTarget(Enemy owner, Player target) {
        // auch egal
        return this;
    }

    @Override
    public Behaviour lostSightContact(Enemy owner, Player target) {
        // Ziel suchen und hinlaufen:
        return new FollowerApproachTargetBehaviour(owner, target);
    }
}
