package de._13ducks.spacebatz.server.ai.behaviour.impl.kamikaze;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericLurkBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 * @author michael
 */
public class KamikazeLurkBehaviour extends GenericLurkBehaviour {

    public KamikazeLurkBehaviour(Enemy owner) {
        super(owner);
    }

    @Override
    public Behaviour targetSpotted(Player target) {
        return new KamikazeApproachTargetBehaviour(owner, target);
    }
}
