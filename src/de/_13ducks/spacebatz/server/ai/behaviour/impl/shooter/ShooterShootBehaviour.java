package de._13ducks.spacebatz.server.ai.behaviour.impl.shooter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericWatchTargetBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class ShooterShootBehaviour extends GenericWatchTargetBehaviour {

    private Player target;

    public ShooterShootBehaviour(Enemy owner, Player target) {
        super(owner, target, 4, 9);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        // schießen wenn möglich:
        owner.getShootAbility().tryUseOnPosition(owner, target.getX(), target.getY());
        return super.tick(gameTick);
    }

    @Override
    public Behaviour onTargetDeath() {
        return new ShooterLurkBehaviour(owner);
    }

    @Override
    public Behaviour toCloseToTarget(Enemy owner, Player target) {
        // egal
        return this;
    }

    @Override
    public Behaviour toFarFromTarget(Enemy owner, Player target) {
        return new ShooterAproachDirectBehaviour(owner, target);
    }

    @Override
    public Behaviour lostSightContact(Enemy owner, Player target) {
        return new ShooterApproachIndirectBehaviour(owner, target);
    }
}
