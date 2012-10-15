package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Ein Item wird einem Client ins Inventar getan
 * @author michael
 */
public class STC_ITEM_DROP extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Item wird gedroppt    
        int clientid = Bits.getInt(data, 0);

        byte[] seritem = new byte[data.length - 4];
        System.arraycopy(data, 4, seritem, 0, seritem.length);
        try {
            ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(seritem));
            Item item = (Item) is.readObject();

            if (clientid == GameClient.getClientID()) {
                GameClient.addToInventory(item);
            }

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

    /**
     * Item wird gedroppt
     */
    public static void sendItemDrop(byte[] seritem, int clientID) {
        byte[] data = new byte[seritem.length + 4];
        Bits.putInt(data, 0, clientID);
        System.arraycopy(seritem, 0, data, 4, seritem.length);
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_SPAWN_ITEM, data), c);
        }
    }
}
