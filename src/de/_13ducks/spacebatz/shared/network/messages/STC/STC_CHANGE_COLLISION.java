package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_CHANGE_COLLISION extends FixedSizeSTCCommand {

    public STC_CHANGE_COLLISION() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Geänderten Boden übernehmen:
        int tx = Bits.getInt(data, 0);
        int ty = Bits.getInt(data, 4);
        int newCollision = Bits.getInt(data, 8);
        if (newCollision == 1) {
            Client.currentLevel.getCollisionMap()[tx][ty] = true;
        } else {
            Client.currentLevel.getCollisionMap()[tx][ty] = false;
        }
    }
}
