package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
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
            GameClient.setItemMap(items);
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

    public static void sendAllItems(Client client, HashMap<Integer, Item> items) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_ITEMS, Server.game.getSerializedItems(), client);
    }
}
