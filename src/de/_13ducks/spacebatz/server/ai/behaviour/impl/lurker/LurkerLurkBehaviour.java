package de._13ducks.spacebatz.server.ai.behaviour.impl.lurker;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.GenericLurkBehaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;

/**
 * Tarnt sich automatisch. Enttarnt sich wenn ein Spieler zu nahe kommt und
 * wechselt zum LurkerAttackBehaviour.
 *
 * @author michael
 */
public class LurkerLurkBehaviour extends GenericLurkBehaviour {

    private boolean visible;

    public LurkerLurkBehaviour(Enemy owner) {
        super(owner);
        visible = true;
    }

    @Override
    public Behaviour targetSpotted(Player target) {
        target.hunters.add(owner);
        owner.setInvisible(false);
        return new LurkerAttackBehaviour(owner, target);
    }

    @Override
    /**
     * Tarnen, sobald zum erstenmal Tick() aufgerufen wird. Das ist nötig, weil
     * dieser Gegner beim Aufruf des Konstruktors noch nicht bei den Clients
     * bekannt ist, und daher dort nicht mit setInvisible() unsichtbar gemacht
     * werden kann.
     */
    public Behaviour tick(int gameTick) {
        if (visible) {
            visible = false;
            owner.setInvisible(true);
        }
        return super.tick(gameTick);
    }

    @Override
    public Behaviour onAttackTarget(Player target) {
        return new LurkerAttackBehaviour(owner, target);
    }
}
