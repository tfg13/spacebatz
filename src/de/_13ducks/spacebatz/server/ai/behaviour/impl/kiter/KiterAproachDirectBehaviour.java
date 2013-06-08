package de._13ducks.spacebatz.server.ai.behaviour.impl.kiter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericDirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class KiterAproachDirectBehaviour extends GenericDirectPursuitBehaviour {

    public KiterAproachDirectBehaviour(Enemy owner, Player target) {
        super(owner, target, 5);
    }

    @Override
    public Behaviour lostSight(Player target, Enemy owner) {
        return new KiterApproachIndirectBehaviour(owner, target);
    }

    @Override
    public Behaviour reachedDesiredDistance(Player target, Enemy owner) {
        owner.move.stopMovement();
        return new KiterShootBehaviour(owner, target);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new KiterLurkBehaviour(owner);
    }
}
