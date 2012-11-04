package de._13ducks.spacebatz.server.ai;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.util.Distance;
import java.util.Iterator;

/**
 * Das Standardverhalten von Mobs: Sie rennen Luftlinie auf den Spieler zu.
 *
 * @author michael
 */
public class StandardMobBehaviour extends Behaviour {

    /**
     * Der Besitzer dieses Behaviours.
     */
    private Enemy owner;
    /**
     * Das Ziel, das verfolgt wird.
     */
    private Entity target;
    /**
     * Ob das Ziel gerae verfolgt wird oder wir zu nahe dran sind.
     */
    private boolean following;

    /**
     * Initialisiert das Behaviour.
     *
     * @param owner
     */
    public StandardMobBehaviour(Enemy owner) {
        super(5);
        this.owner = owner;
        Iterator<Client> iter = Server.game.clients.values().iterator();
        while (iter.hasNext()) {
            target = iter.next().getPlayer();
        }
        owner.setFollowMode(target);
    }

    @Override
    protected void onTick(int gameTick) {
        if (Distance.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) < 2.0) {
            // Wenn wir zu nahe dran sind anhalten:
            owner.stopFollowMode();
            following = false;
        } else if (!following && Distance.getDistance(owner.getX(), owner.getY(), target.getX(), target.getY()) > 3.0) {
            // wenn wir gerade angehalten sind, aber das target zu weit weg ist, wieder loslaufen:
            owner.setFollowMode(target);
            following = true;
        }
    }
}
