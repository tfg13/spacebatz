package de._13ducks.spacebatz.server.ai.behaviour.impl.summoner;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericWatchTargetBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

class SummonerSummonBehaviour extends GenericWatchTargetBehaviour {
    
    private Player target;
    
    public SummonerSummonBehaviour(Enemy owner, Player target) {
        super(owner, target, 7, 10);
        this.target = target;
    }
    
    @Override
    public Behaviour tick(int gameTick) {
        // schießen wenn möglich:
        owner.getShootAbility().tryUseOnPosition(owner, owner.getX(), owner.getY());
        return super.tick(gameTick);
    }
    
    @Override
    public Behaviour onTargetDeath() {
        return new SummonerLurkBehaviour(owner);
    }
    
    @Override
    public Behaviour toCloseToTarget(Enemy owner, Player target) {
        // Zu nah dran, wegrennen
        owner.setSpeed(owner.maxSpeed);
        double dx = (owner.getX() - target.getX());
        double dy = (owner.getY() - target.getY());
        owner.move.setVector(dx, dy);
        return this;
    }
    
    @Override
    public Behaviour toFarFromTarget(Enemy owner, Player target) {
        return new SummonerAproachDirectBehaviour(owner, target);
    }
    
    @Override
    public Behaviour lostSightContact(Enemy owner, Player target) {
        return new SummonerLurkBehaviour(owner);
    }
}
