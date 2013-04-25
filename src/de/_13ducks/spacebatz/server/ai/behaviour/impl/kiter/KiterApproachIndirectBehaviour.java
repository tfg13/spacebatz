package de._13ducks.spacebatz.server.ai.behaviour.impl.kiter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class KiterApproachIndirectBehaviour extends GenericIndirectPursuitBehaviour {

    public KiterApproachIndirectBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        return new KiterAproachDirectBehaviour(owner, target);
    }

    @Override
    public Behaviour targetLost() {
        return new KiterLurkBehaviour(owner);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new KiterLurkBehaviour(owner);
    }
}
