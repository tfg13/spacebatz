package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.client.graphics.Fx;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_EQUIP_ITEM extends FixedSizeSTCCommand {

    public STC_EQUIP_ITEM() {
        super(9);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will ein bestimmtes Item anlegen
        int netIDItem3 = Bits.getInt(data, 0); // netID des  Items
        byte selslot = data[4];
        int clientID4 = Bits.getInt(data, 5); // clientID des Spielers
        if (clientID4 == Client.getClientID()) {
            Item item = Client.getInventoryItems().get(netIDItem3);
            Client.getEquippedItems().getEquipslots()[(int) item.getItemClass()][selslot] = item;
            for (int i = 0; i < Client.getInventorySlots().length; i++) {
                if (Client.getInventorySlots()[i] != null && Client.getInventorySlots()[i].equals(item.getInventoryslot())) {
                    Client.getInventorySlots()[i] = null;
                }
            }
            Client.getInventoryItems().values().remove(item);
        }
    }
}
