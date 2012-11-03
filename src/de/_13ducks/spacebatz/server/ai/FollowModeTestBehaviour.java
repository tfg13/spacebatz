package de._13ducks.spacebatz.server.ai;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import java.util.Iterator;

/**
 *
 * @author michael
 */
public class FollowModeTestBehaviour extends Behaviour {

    private boolean modeSet;
    private Enemy owner;

    public FollowModeTestBehaviour(Enemy owner) {
        super(5);
        this.owner = owner;
    }

    @Override
    protected void onTick(int gameTick) {
        if (!modeSet) {
            Iterator<Client> iter = Server.game.clients.values().iterator();
            while (iter.hasNext()) {
                owner.setFollowMode(iter.next().getPlayer());
                modeSet = true;
                return;
            }

        }
    }
}
