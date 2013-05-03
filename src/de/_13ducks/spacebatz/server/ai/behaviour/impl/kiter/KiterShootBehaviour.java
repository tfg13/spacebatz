package de._13ducks.spacebatz.server.ai.behaviour.impl.kiter;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericStandDivergeBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

class KiterShootBehaviour extends GenericStandDivergeBehaviour {
    
    private Player target;
    
    public KiterShootBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }
    
    @Override
    public Behaviour tick(int gameTick) {
        // schießen wenn möglich:
        owner.getShootAbility().tryUseOnPosition(owner, target.getX(), target.getY());
        double distance = GeoTools.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY());
        if (distance < 4) {
            // Zu nah dran, wegrennen
            double dx = (owner.getX() - target.getX());
            double dy = (owner.getY() - target.getY());
            owner.move.setVector(dx, dy);
            return super.tick(gameTick);
        } else if (distance > 7) {
            // Zu weit weg, hinrennen
            return new KiterApproachIndirectBehaviour(owner, target);
        } else {
            // Genau ruchtig, stehenbleiben
            return super.tick(gameTick);
        }
    }
    
    @Override
    public Behaviour onTargetDeath() {
        return new KiterLurkBehaviour(owner);
    }
}
