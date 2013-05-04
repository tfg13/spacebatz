package de._13ducks.spacebatz.server.ai.behaviour.impl.kiter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericWatchTargetBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class KiterShootBehaviour extends GenericWatchTargetBehaviour {

    private Player target;

    public KiterShootBehaviour(Enemy owner, Player target) {
        super(owner, target, 4, 7);
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
        return new KiterLurkBehaviour(owner);
    }

    @Override
    public Behaviour toCloseToTarget(Enemy owner, Player target) {
        // Zu nah dran, wegrennen
        double dx = (owner.getX() - target.getX());
        double dy = (owner.getY() - target.getY());
        owner.move.setVector(dx, dy);
        return this;
    }

    @Override
    public Behaviour toFarFromTarget(Enemy owner, Player target) {
        // Zu weit weg, hinrennen
        return new KiterApproachIndirectBehaviour(owner, target);
    }

    @Override
    public Behaviour lostSightContact(Enemy owner, Player target) {
        return new KiterApproachIndirectBehaviour(owner, target);
    }
}
