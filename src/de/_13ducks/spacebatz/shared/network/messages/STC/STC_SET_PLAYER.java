package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SET_PLAYER extends FixedSizeSTCCommand {

    public STC_SET_PLAYER() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Player setzen
        GameClient.player = new Player(Bits.getInt(data, 0));
        GameClient.netIDMap.put(GameClient.player.netID, GameClient.player);
    }
}
