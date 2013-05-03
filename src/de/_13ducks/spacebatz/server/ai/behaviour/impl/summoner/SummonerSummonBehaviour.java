package de._13ducks.spacebatz.server.ai.behaviour.impl.summoner;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericStandDivergeBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

class SummonerSummonBehaviour extends GenericStandDivergeBehaviour {

    private Player target;

    public SummonerSummonBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        // schießen wenn möglich:
        owner.getShootAbility().tryUseOnPosition(owner, owner.getX(), owner.getY());
        double distance = GeoTools.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY());
        if (distance < 5) {
            // Zu nah dran, wegrennen
            double dx = (owner.getX() - target.getX());
            double dy = (owner.getY() - target.getY());
            owner.move.setVector(dx, dy);
            return this;
        } else if (distance > 10) {
            // Zu weit weg, hinrennen
            return new SummonerApproachIndirectBehaviour(owner, target);
        } else {
            // Genau ruchtig, stehenbleiben
            return super.tick(gameTick);
        }
    }

    @Override
    public Behaviour onTargetDeath() {
        return new SummonerLurkBehaviour(owner);
    }
}
