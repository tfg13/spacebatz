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

    /**
     * Item wird von Spieler aufgesammelt und auf ein anderes draufgestackt
     */
    public static void sendItemGrabToStack(int newitemnetID, int clientID, int stackitemID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[12];
            Bits.putInt(b, 0, newitemnetID);
            Bits.putInt(b, 4, clientID);
            Bits.putInt(b, 8, stackitemID);

            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_GRAB_ITEM_TO_STACK, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_GRAB_ITEM_TO_STACK, b), c);

        }
    }
}
