package de._13ducks.spacebatz.server.ai.behaviour.impl.shooter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Distance;

class ShooterShootBehaviour extends Behaviour {

    private Player target;

    public ShooterShootBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        // schießen wenn möglich:
        owner.getShootAbility().tryUseOnPosition(owner, target.getX(), target.getY());
        if (Distance.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) < 4) {
            return this;
        } else {
            return new ShooterApproachIndirectBehaviour(owner, target);
        }
    }
    
     @Override
    public Behaviour onTargetDeath() {
        return new ShooterLurkBehaviour(owner);
    }
    
}
