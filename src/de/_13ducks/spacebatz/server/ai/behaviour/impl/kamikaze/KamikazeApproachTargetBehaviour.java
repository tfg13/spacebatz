package de._13ducks.spacebatz.server.ai.behaviour.impl.kamikaze;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class KamikazeApproachTargetBehaviour extends GenericIndirectPursuitBehaviour {

    public KamikazeApproachTargetBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        return new KamikazeAproachDirectBehaviour(owner, target);
    }

    @Override
    public Behaviour targetLost() {
        return new KamikazeLurkBehaviour(owner);
    }
}
