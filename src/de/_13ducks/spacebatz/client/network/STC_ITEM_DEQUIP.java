package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_ITEM_DEQUIP extends FixedSizeSTCCommand {

    public STC_ITEM_DEQUIP() {
        super(10);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will ein bestimmtes Item ablegen
        int slottype = Bits.getInt(data, 0); // netID des  Items
        byte selslot2 = data[4];
        byte droptoground = data[5];
        int clientID2 = Bits.getInt(data, 6); // clientID des Spielers
        if (clientID2 == Client.getClientID()) {
            Item item = Client.getEquippedItems().getEquipslots()[slottype][selslot2];
            Client.getEquippedItems().getEquipslots()[slottype][selslot2] = null;
            if (droptoground == 0) {
                Client.addToInventory(item);
            }
        }
    }
}
