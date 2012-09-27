package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
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
        if (clientID == GameClient.getClientID()) {
            Item item = GameClient.getItemMap().get(netIDItem2);

            GameClient.addToInventory(item);
        }
        GameClient.getItemMap().remove(netIDItem2);
    }

    /**
     * Item (nicht Material) wird von Spieler aufgesammelt und kriegt eigenen Itemslot
     */
    public static void sendItemGrab(int itemnetID, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[8];
            Bits.putInt(b, 0, itemnetID);
            Bits.putInt(b, 4, clientID);

            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_GRAB_ITEM, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_GRAB_ITEM, b), c);
        }
    }
}
