package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SWITCH_WEAPON extends FixedSizeSTCCommand {

    public STC_SWITCH_WEAPON() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will andere Waffe auswählen
        int clientid = Bits.getInt(data, 0);
        byte wslot = data[4];
        if (clientid == Client.getClientID()) {
            Client.getPlayer().setSelectedattack(wslot);
        }
    }
}
