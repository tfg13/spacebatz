package de._13ducks.spacebatz.server.ai.behaviour.impl.shooter;

import de._13ducks.spacebatz.server.ai.behaviour.impl.spectator.*;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericLurkBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 *
 * @author michael
 */
public class ShooterLurkBehaviour extends GenericLurkBehaviour {

    public ShooterLurkBehaviour(Enemy owner) {
        super(owner);
    }

    @Override
    public Behaviour targetSpotted(Player target) {
        target.hunters.add(owner);
        return new ShooterApproachIndirectBehaviour(owner, target);
    }

    @Override
    public Behaviour onAttackTarget(Player target) {
        return new ShooterApproachIndirectBehaviour(owner, target);
    }
}
