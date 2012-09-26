package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_GRAB_ITEM_TO_STACK extends FixedSizeSTCCommand {

    public STC_GRAB_ITEM_TO_STACK() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Item wird aufgesammelt
        int newnetIDItem3 = Bits.getInt(data, 0); // netID des aufgesammelten Items
        int clientID3 = Bits.getInt(data, 4); // netID des Spielers, der es aufgesammelt hat
        int stacknetID = Bits.getInt(data, 8); // die netID des Items, auf das gestackt werden soll

        // Item ins Client-Inventar verschieben, wenn eigene clientID
        if (clientID3 == GameClient.getClientID()) {
            Item item = GameClient.getItemMap().get(newnetIDItem3);

            // Item soll gestackt werden
            if (item.getName().equals("Money")) {
                GameClient.setMaterial(0, item.getAmount());
            } else {
                Item itemStack = GameClient.getInventoryItems().get(stacknetID);
                itemStack.setAmount(itemStack.getAmount() + item.getAmount());
            }

        }
        GameClient.getItemMap().remove(newnetIDItem3);
    }
}
