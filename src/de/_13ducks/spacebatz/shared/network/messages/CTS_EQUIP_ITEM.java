package de._13ducks.spacebatz.shared.network.messages;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class CTS_EQUIP_ITEM extends FixedSizeCTSCommand {

    public CTS_EQUIP_ITEM() {
        super(9);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int netID = Bits.getInt(data, 0);
        byte selectedslot = data[4];
        client.getPlayer().clientEquipItem(netID, selectedslot);
    }
}
