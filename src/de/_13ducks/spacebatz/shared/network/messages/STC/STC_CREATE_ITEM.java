package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Sendet ein neues Item an einen Client.
 *
 * @author mekhar
 */
public class STC_CREATE_ITEM extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Item wird gedroppt
        int slot = Bits.getInt(data, 0);

        byte[] seritem = new byte[data.length - 4];
        System.arraycopy(data, 4, seritem, 0, seritem.length);
        try {
            ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(seritem));
            Item item = (Item) is.readObject();
            GameClient.getEngine().getGraphics().getInventory().createItem(slot, item);

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        throw new IllegalStateException("STC_CREATE_ITEM wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    @Override
    public int getSize(byte sizeData) {
        throw new IllegalStateException("STC_CREATE_ITEM wird nie als einzelpacket gesendet, also muss es seine Größe nicht wissen.");
    }

    public static void sendCreateItem(Item item, int slot, Client client) {
        byte[] serializedItem = null;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(item);
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedItem = bs.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        byte[] data = new byte[serializedItem.length + 4];
        System.arraycopy(serializedItem, 0, data, 4, serializedItem.length);
        Bits.putInt(data, 0, slot);

        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_CREATE_ITEM, data), client);
    }
}
