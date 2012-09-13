package de._13ducks.spacebatz.client.network.messages;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.shared.Item;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *
 * @author michael
 */
public class STC_ITEM_DROP extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Item wird gedroppt    
        try {
            ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(data));
            Item item = (Item) is.readObject();
            Client.getItemMap().put(item.getNetID(), item);

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        throw new IllegalStateException("STC_ITEM_DROP wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    @Override
    public int getSize(byte sizeData) {
        throw new IllegalStateException("STC_ITEM_DROP wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }
}
