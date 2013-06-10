package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.EntityLinearTargetObserver;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Calculates a path and follows that path untill the target is in direct
 * sightline. Recalculates the path regularly.
 *
 * @author michael
 */
public abstract class GenericIndirectPursuitBehaviour extends Behaviour implements EntityLinearTargetObserver {

    /**
     * Die Zeit, nach der die Verfolgung abgebrochen wird.
     */
    private int MAX_PURSUIT_TIME = 60 * 10000;
    /**
     * Das Intervall in Ticks das vergeht bis ein neuer Weg berechnet wird.
     */
    private final int RECALC_WAY_INTERVALL = 20;
    /**
     * Der Spieler, der verfolgt wird.
     */
    private Player target;
    /**
     * Der Tick, in dem dieses Behaviour berechnet wird zwecks loadbalancing.
     */
    private int myTick;

    /**
     * Erzeugt ein neues IndirectPursuitBevahoiur.
     *
     * @param enemy
     * @param target
     */
    public GenericIndirectPursuitBehaviour(Enemy enemy, Player target) {
        super(enemy);
        this.target = target;
        myTick = (int) Math.random() * RECALC_WAY_INTERVALL;
        owner.setLookInMovingDirection(true);
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            owner.setLastSightContact(Server.game.getTick());
            owner.setLookInMovingDirection(false);
            return targetInSight(owner, target);
        } else {
            if (Server.game.getTick() - owner.getLastSightContact() > MAX_PURSUIT_TIME) {
                owner.setLookInMovingDirection(false);
                return targetLost();
            }
            if (gameTick % RECALC_WAY_INTERVALL == myTick) {
                Vector start = new Vector(owner.getX(), owner.getY());
                Vector goal = new Vector(target.getX(), target.getY());
                Server.game.pathfinder.requestPath(start, goal, owner, owner.getSize());
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
        if (path != null && path.length > 1) {
            owner.followPath(path);
        }
        return this;
    }

    /**
     * Wird aufgerufen, wenn das Ziel länger als MAX_PURSUIT_TIME nicht in Sicht
     * war.
     *
     * @return Das Verhalten, zu dem gewechselt werden soll
     */
    public abstract Behaviour targetLost();

    @Override
    public void movementAborted() {
    }

    @Override
    public void movementBlocked() {
    }

    @Override
    public void targetReached() {
        tick(Server.game.getTick());
    }
}
