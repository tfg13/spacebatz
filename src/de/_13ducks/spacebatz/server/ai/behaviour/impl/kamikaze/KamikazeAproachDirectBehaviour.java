package de._13ducks.spacebatz.server.ai.behaviour.impl.kamikaze;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericDirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class KamikazeAproachDirectBehaviour extends GenericDirectPursuitBehaviour {

    public KamikazeAproachDirectBehaviour(Enemy owner, Player target) {
        super(owner, target, 2);
    }

    @Override
    public Behaviour lostSight(Player target, Enemy owner) {
        return new KamikazeApproachTargetBehaviour(owner, target);
    }

    @Override
    public Behaviour reachedDesiredDistance(Player target, Enemy owner) {
        owner.move.stopMovement();
        owner.getShootAbility().tryUseOnPosition(owner, 0, 0);
        Server.game.getEntityManager().removeEntity(owner.netID);
        return new KamikazeLurkBehaviour(owner);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new KamikazeLurkBehaviour(owner);
    }
}
