package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_GRAB_ITEM extends FixedSizeSTCCommand {

    public STC_GRAB_ITEM() {
        super(8);
    }

    @Override
    public void execute(byte[] data) {
        // Item wird aufgesammelt
        int netIDItem2 = Bits.getInt(data, 0); // netID des aufgesammelten Items
        int clientID = Bits.getInt(data, 4); // netID des Spielers, der es aufgesammelt hat
        // Item ins Client-Inventar verschieben, wenn eigene clientID
        if (clientID == Client.getClientID()) {
            Item item = Client.getItemMap().get(netIDItem2);

            Client.addToInventory(item);
        }
        Client.getItemMap().remove(netIDItem2);
    }
}
