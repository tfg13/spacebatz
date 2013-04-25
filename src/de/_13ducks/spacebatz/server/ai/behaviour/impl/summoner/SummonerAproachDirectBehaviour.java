package de._13ducks.spacebatz.server.ai.behaviour.impl.summoner;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericDirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class SummonerAproachDirectBehaviour extends GenericDirectPursuitBehaviour {

    public SummonerAproachDirectBehaviour(Enemy owner, Player target) {
        super(owner, target, 7);
    }

    @Override
    public Behaviour lostSight(Player target, Enemy owner) {
        return new SummonerApproachIndirectBehaviour(owner, target);
    }

    @Override
    public Behaviour reachedDesiredDistance(Player target, Enemy owner) {
        owner.move.stopMovement();
        return new SummonerSummonBehaviour(owner, target);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new SummonerLurkBehaviour(owner);
    }
}
