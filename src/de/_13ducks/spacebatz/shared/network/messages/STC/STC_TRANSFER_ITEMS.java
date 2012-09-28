package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import java.io.ByteArrayInputStream;
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
            ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
            byteStream.read(); // Erstes Byte ignorieren
            ObjectInputStream is = new ObjectInputStream(byteStream);
            HashMap<Integer, Item> items = (HashMap<Integer, Item>) is.readObject();
            GameClient.setItemMap(items);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return sizeData;
    }

    public static void sendAllItems(Client client, HashMap<Integer, Item> items) {
        byte[] data = Server.game.getSerializedItems();
        byte[] sendData = new byte[data.length + 1];
        sendData[0] = (byte) sendData.length;
        System.arraycopy(data, 0, sendData, 1, data.length);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_TRANSFER_ITEMS, sendData), client);
    }
}
