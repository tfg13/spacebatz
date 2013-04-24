package de._13ducks.spacebatz.server.ai.behaviour.impl.spectator;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class SpectatorWatchBehaviour extends Behaviour {

    private Player target;

    public SpectatorWatchBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
            if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return this;
        } else {
            return new SpectatorApproachTargetBehaviour(owner, target);
        }
    }
    
     @Override
    public Behaviour onTargetDeath() {
        return new SpectatorLurkBehaviour(owner);
    }
}
