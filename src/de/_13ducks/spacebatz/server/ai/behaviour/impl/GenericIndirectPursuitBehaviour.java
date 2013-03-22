package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.EntityLinearTargetObserver;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Calculates a path and follows that path untill the target is in direct sightline.
 * Recalculates the path regularly.
 *
 * @author michael
 */
public abstract class GenericIndirectPursuitBehaviour extends Behaviour implements EntityLinearTargetObserver {

    /**
     * Die Zeit, nach der die Verfolgung abgebrochen wird.
     */
    private int MAX_PURSUIT_TIME = 60 * 10;
    /**
     * Der Spieler, der verfolgt wird.
     */
    private Player target;
    /**
     * Die letzte bekannte Position des Ziels, zu der wir gerade laufen.
     */
    private Vector currentTarget;

    /**
     * Erzeugt ein neues IndirectPursuitBevahoiur.
     *
     * @param enemy
     * @param target
     */
    public GenericIndirectPursuitBehaviour(Enemy enemy, Player target) {
        super(enemy);
        this.target = target;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            owner.setLastSightContact(Server.game.getTick());
            return targetInSight(owner, target);
        } else {
            if (Server.game.getTick() - owner.getLastSightContact() > MAX_PURSUIT_TIME) {
                return targetLost();
            }
            Vector target = getLatestKnownTargetPosition();
            if (target == null) {
                return targetLost();
            } else if (!target.equals(currentTarget)) {
                owner.setLinearTarget(target.x, target.y, this);
            }
            return this;
        }

    }

    /**
     * Wird aufgerufen, wenn das Ziel in Sicht ist.
     *
     * @param owner
     * @param target
     * @return Das Verhalten, zu dem gewechselt werden soll
     */
    public abstract Behaviour targetInSight(Enemy owner, Player target);

    @Override
    public Behaviour pathComputed(Vector[] path) {


        return this;
    }

    /**
     * Wird aufgerufen, wenn das Ziel länger als MAX_PURSUIT_TIME nicht in Sicht war.
     *
     * @return Das Verhalten, zu dem gewechselt werden soll
     */
    public abstract Behaviour targetLost();

    /**
     * Gibt die letzte Position, an der das Ziel gesehen wurde und die vom owner aus erreichbar ist, zurück.
     */
    private Vector getLatestKnownTargetPosition() {
        Vector position = null;
        for (int i = target.getPlayerPath().getBufferSize(); i == 0; i--) {
            Vector targetPosition = target.getPlayerPath().get(i);
            if (owner.lineOfSight(owner.getX(), owner.getY(), targetPosition.x, targetPosition.y)) {
                return targetPosition;
            }
        }
        return position;
    }
}
