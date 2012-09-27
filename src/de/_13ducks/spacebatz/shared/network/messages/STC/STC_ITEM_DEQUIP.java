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
public class STC_ITEM_DEQUIP extends FixedSizeSTCCommand {

    public STC_ITEM_DEQUIP() {
        super(10);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will ein bestimmtes Item ablegen
        int slottype = Bits.getInt(data, 0); // netID des  Items
        byte selslot2 = data[4];
        byte droptoground = data[5];
        int clientID2 = Bits.getInt(data, 6); // clientID des Spielers
        if (clientID2 == GameClient.getClientID()) {
            Item item = GameClient.getEquippedItems().getEquipslots()[slottype][selslot2];
            GameClient.getEquippedItems().getEquipslots()[slottype][selslot2] = null;
            if (droptoground == 0) {
                GameClient.addToInventory(item);
            }
        }
    }

    /**
     * Item wird von Client abgelegt (zurück ins Inventar)
     */
    public static void sendItemDequip(int slottype, byte selslot, byte droptoground, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[10];
            Bits.putInt(b, 0, slottype);
            b[4] = selslot;
            b[5] = droptoground;
            Bits.putInt(b, 6, clientID);
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_DEQUIP_ITEM, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_DEQUIP_ITEM, b), c);
        }
    }
}
