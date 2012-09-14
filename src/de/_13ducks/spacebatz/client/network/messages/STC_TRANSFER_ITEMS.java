package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.shared.Item;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

/**
 *
 * @author michael
 */
public class STC_TRANSFER_ITEMS extends STCCommand {

    @Override
    public void execute(byte[] data) {
        try {
            ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(data));
            HashMap<Integer, Item> items = (HashMap<Integer, Item>) is.readObject();
            Client.setItemMap(items);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        throw new IllegalStateException("STC_TRANSFER_ITEMS wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    @Override
    public int getSize(byte sizeData) {
        throw new IllegalStateException("STC_TRANSFER_ITEMS wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }
}
