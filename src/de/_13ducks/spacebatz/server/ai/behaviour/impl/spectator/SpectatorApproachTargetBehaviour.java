package de._13ducks.spacebatz.server.ai.behaviour.impl.spectator;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class SpectatorApproachTargetBehaviour extends GenericIndirectPursuitBehaviour {

    public SpectatorApproachTargetBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        owner.move.stopMovement();
        return new SpectatorWatchBehaviour(owner, target);
    }

    @Override
    public Behaviour targetLost() {
        return new SpectatorLurkBehaviour(owner);
    }
}
