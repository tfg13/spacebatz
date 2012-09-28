package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
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
            GameClient.getItemMap().put(item.getNetID(), item);

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
    public static void sendItemDrop(byte[] seritem) {
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(MessageIDs.NET_TCP_CMD_SPAWN_ITEM, seritem, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_SPAWN_ITEM, seritem), c);
        }
    }
}
