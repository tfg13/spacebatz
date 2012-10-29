/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author House of Nikolaus
 */
public class STC_INV_ITEM_MOVE extends FixedSizeSTCCommand {

    public STC_INV_ITEM_MOVE() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        int inventoryslot1 = Bits.getInt(data, 0);
        int inventoryslot2 = Bits.getInt(data, 4);
        int clientID = Bits.getInt(data, 8);

        if (clientID == GameClient.getClientID()) {
            if (GameClient.getItems()[inventoryslot2] == null) {
                // angeklickter Slot leer -> Item verschieben
                GameClient.getItems()[inventoryslot2] = GameClient.getItems()[inventoryslot1];
                GameClient.getItems()[inventoryslot1] = null;
            } else {
                // angeklickter Slot belegt -> Items tauschen
                Item swapSlot = GameClient.getItems()[inventoryslot2];
                GameClient.getItems()[inventoryslot2] = GameClient.getItems()[inventoryslot1];
                GameClient.getItems()[inventoryslot1] = swapSlot;
            }
        }
    }

    public static void sendInvItemMove(int inventoryslot1, int inventoryslot2, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[12];
            Bits.putInt(b, 0, inventoryslot1);
            Bits.putInt(b, 4, inventoryslot2);
            Bits.putInt(b, 8, clientID);

            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_INV_ITEM_MOVE, b), c);
        }
    }
}
