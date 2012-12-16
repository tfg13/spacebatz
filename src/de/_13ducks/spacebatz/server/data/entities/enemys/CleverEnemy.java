package de._13ducks.spacebatz.server.data.entities.enemys;

import de._13ducks.spacebatz.client.graphics.controls.GodControl;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.astar.PathRequester;
import de._13ducks.spacebatz.server.ai.astar.PrecisePosition;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;

/**
 *
 * @author michael
 */
public class CleverEnemy extends Enemy implements PathRequester {

    private int lastRequest;
    private Entity target;
    private boolean waiting;

    public CleverEnemy(double x, double y, int netID, int enemyTypeID) {
        super(x, y, netID, 1);
        target = Server.game.clients.values().iterator().next().getPlayer();
    }

    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        if (gameTick - lastRequest > 300 && !waiting) {
            Server.game.pathfinder.requestPath(new PrecisePosition(getX(), getY()), new PrecisePosition(target.getX(), target.getY()), this, getSize());
            lastRequest = gameTick;
            waiting = true;
        }
    }

    @Override
    public void pathComputed(PrecisePosition[] path) {
        waiting = false;
        if (path.length > 1) {
            followPath(path);

            synchronized (GodControl.debugPath) {
                GodControl.debugPath = path;
            }
        } else {
//            System.out.println("bad path.");
        }
    }
}
