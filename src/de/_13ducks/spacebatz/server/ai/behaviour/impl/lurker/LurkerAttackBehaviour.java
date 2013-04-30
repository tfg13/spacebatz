package de._13ducks.spacebatz.server.ai.behaviour.impl.lurker;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

/**
 * Wartet eine bestimmte Zeit, schießt dann jeden Tick auf das Ziel.
 *
 * @author michael
 */
class LurkerAttackBehaviour extends Behaviour {

    /**
     * Zeit zum Angriff in Ticks
     */
    private int timeTillAttack = 200;
    /**
     * Das Ziel
     */
    private Player target;

    public LurkerAttackBehaviour(Enemy owner, Player target) {
        super(owner);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (GeoTools.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) > owner.getProperties().getSightrange()) {
            return new LurkerLurkBehaviour(owner);
        } else if (timeTillAttack == 0) {
            owner.getShootAbility().tryUseOnPosition(owner, target.getX(), target.getY());
        } else {
            timeTillAttack--;

        }
        return this;
    }

    @Override
    public Behaviour onTargetDeath() {
        return new LurkerLurkBehaviour(owner);
    }
}
