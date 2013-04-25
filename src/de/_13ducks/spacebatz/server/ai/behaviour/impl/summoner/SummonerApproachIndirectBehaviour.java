package de._13ducks.spacebatz.server.ai.behaviour.impl.summoner;

import de._13ducks.spacebatz.server.ai.behaviour.impl.kiter.*;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class SummonerApproachIndirectBehaviour extends GenericIndirectPursuitBehaviour {

    public SummonerApproachIndirectBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        return new SummonerAproachDirectBehaviour(owner, target);
    }

    @Override
    public Behaviour targetLost() {
        return new SummonerLurkBehaviour(owner);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new SummonerLurkBehaviour(owner);
    }
}
