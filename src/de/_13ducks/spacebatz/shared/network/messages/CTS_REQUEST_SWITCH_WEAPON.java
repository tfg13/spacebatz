package de._13ducks.spacebatz.shared.network.messages;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;

/**
 *
 * @author michael
 */
public class CTS_REQUEST_SWITCH_WEAPON extends FixedSizeCTSCommand {

    public CTS_REQUEST_SWITCH_WEAPON() {
        super(1);
    }

    @Override
    public void execute(Client client, byte[] data) {
        byte selslot2 = data[0];
        client.getPlayer().clientSelectWeapon(selslot2);
    }
}
