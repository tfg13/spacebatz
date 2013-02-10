package de._13ducks.spacebatz.server.ai.behaviour.impl.cleverenemy;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 *
 * @author michael
 */
public class CleverEnemyBehaviour extends Behaviour {

    private int lastRequest;
    private Entity target;
    private boolean waiting;

    public CleverEnemyBehaviour(Enemy enemy) {
        super(enemy);
        target = Server.game.clients.values().iterator().next().getPlayer();
    }

    @Override
    public Behaviour tick(int gameTick) {
        super.tick(gameTick);
        if (gameTick - lastRequest > 300 && !waiting) {
            Server.game.pathfinder.requestPath(new Vector(owner.getX(), owner.getY()), new Vector(target.getX(), target.getY()), owner, owner.getSize());
            lastRequest = gameTick;
            waiting = true;
        }
        return this;
    }

    @Override
    public Behaviour pathComputed(Vector[] path) {
        waiting = false;
        if (path.length > 1) {
            owner.followPath(path);
        }
        return this;
    }
}
