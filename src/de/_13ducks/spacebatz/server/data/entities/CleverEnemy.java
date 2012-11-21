package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.client.graphics.controls.GodControl;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.astar.PathRequester;
import de._13ducks.spacebatz.server.ai.astar.PrecisePosition;

/**
 *
 * @author michael
 */
public class CleverEnemy extends Enemy implements PathRequester {

    private int lastRequest;
    private Entity target;

    public CleverEnemy(double x, double y, int netID, int enemyTypeID) {
        super(x, y, netID, 1);
        target = Server.game.clients.values().iterator().next().getPlayer();
    }

    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        if (gameTick - lastRequest > 100) {
            Server.game.pathfinder.requestPath(new PrecisePosition((int) getX(), (int) getY()), new PrecisePosition(target.getX(), target.getY()), this, speed);
            lastRequest = gameTick;
        }
    }

    @Override
    public void pathComputed(PrecisePosition[] path) {
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
