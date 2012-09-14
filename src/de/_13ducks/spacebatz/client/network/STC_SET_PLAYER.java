package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SET_PLAYER extends FixedSizeSTCCommand {

    public STC_SET_PLAYER() {
        super(4);
    }

    @Override
    public void execute(byte[] data) {
        // Player setzen
        Client.player = new Player(Bits.getInt(data, 0));
        Client.netIDMap.put(Client.player.netID, Client.player);
    }
}
