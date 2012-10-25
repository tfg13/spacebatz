package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_EQUIP_ITEM extends FixedSizeSTCCommand {

    public STC_EQUIP_ITEM() {
        super(9);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will ein bestimmtes Item anlegen
        int netIDItem3 = Bits.getInt(data, 0); // netID des  Items
        byte selslot = data[4];
        int clientID4 = Bits.getInt(data, 5); // clientID des Spielers

        if (clientID4 == GameClient.getClientID()) {

            for (int i = 0; i < GameClient.getItems().length; i++) {
                if (GameClient.getItems()[i].getNetID() == netIDItem3) {

                    Item item = GameClient.getItems()[i];
                    GameClient.getEquippedItems().getEquipslots()[(int) item.getItemClass()][selslot] = item;

                    GameClient.getItems()[i] = null;

                    break;
                }
            }

        }
    }

    /**
     * Item wird von Client angelegt
     */
    public static void sendItemEquip(int itemnetID, byte selslot, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[9];
            Bits.putInt(b, 0, itemnetID);
            b[4] = selslot;
            Bits.putInt(b, 5, clientID);
            //Server.serverNetwork.sendTcpData(MessageIDs.NET_TCP_CMD_EQUIP_ITEM, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_EQUIP_ITEM, b), c);
        }
    }
}
