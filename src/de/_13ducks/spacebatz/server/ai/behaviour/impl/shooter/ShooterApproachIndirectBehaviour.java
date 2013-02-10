package de._13ducks.spacebatz.server.ai.behaviour.impl.shooter;

import de._13ducks.spacebatz.server.ai.behaviour.impl.spectator.*;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericIndirectPursuitBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class ShooterApproachIndirectBehaviour extends GenericIndirectPursuitBehaviour {

    public ShooterApproachIndirectBehaviour(Enemy owner, Player target) {
        super(owner, target);
    }

    @Override
    public Behaviour targetInSight(Enemy owner, Player target) {
        return new ShooterAproachDirectBehaviour(owner, target);
    }
}
