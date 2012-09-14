package de._13ducks.spacebatz.shared.network.messages;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class CTS_REQUEST_ITEM_DEQUIP extends FixedSizeCTSCommand {

    public CTS_REQUEST_ITEM_DEQUIP() {
        super(5);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int slottype = Bits.getInt(data, 0);
        byte selslot = data[4];
        client.getPlayer().clientDequipItem(slottype, selslot);
    }
}
