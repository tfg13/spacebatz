package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_BROADCAST_GROUND_CHANGE extends FixedSizeSTCCommand {

    public STC_BROADCAST_GROUND_CHANGE() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Geänderten Boden übernehmen:
        int x = Bits.getInt(data, 0);
        int y = Bits.getInt(data, 4);
        int newGround = Bits.getInt(data, 8);
        Client.currentLevel.getGround()[x][y] = newGround;
    }
}
