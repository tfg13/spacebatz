package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Distance;

/**
 * Lässt den Besitzer in Luftlinie zu sienem Ziel laufen.
 *
 * @author michael
 */
public abstract class GenericDirectPursuitBehaviour extends Behaviour {

    /**
     * Das Ziel, an das sich der Besitzer nähert.
     */
    private Player target;
    /**
     * Die gewünschte Distanz zum Ziel.
     */
    private double desiredDistance;

    /**
     * Initialisiert dieses Behaviour.
     *
     * @param owner der Gegner, der dieses Behaviour ausführt
     * @param target das Ziel, an das der Besitzer sich nähern soll
     * @param desiredDistance die gewünschte Distanz zum Ziel, bei der reachedDesiredDistance() zurückgegeben wird
     */
    public GenericDirectPursuitBehaviour(Enemy owner, Player target, int desiredDistance) {
        super(owner);
        this.target = target;
        this.desiredDistance = desiredDistance;
    }

    @Override
    public Behaviour tick(int gameTick) {
        if (!owner.lineOfSight(owner.getX(), owner.getY(), target.getX(), target.getY())) {
            return lostSight(target, owner);
        } else if (Distance.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) < desiredDistance) {
            return reachedDesiredDistance(target, owner);
        } else {
            if (!owner.move.isFollowingTarget(target)) {
                owner.move.setFollowTarget(target);
            }
            // Läuft schon, machen lassen.
            return this;
        }
    }

    /**
     * Called when we lost sight of our target.
     *
     * @param target
     * @param owner
     * @return
     */
    public abstract Behaviour lostSight(Player target, Enemy owner);

    /**
     * Called when the desired Distance is reached,
     *
     * @param target
     * @param owner
     * @return
     */
    public abstract Behaviour reachedDesiredDistance(Player target, Enemy owner);
}
