/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Verschiebt Item im Inventar / Tauscht 2 Items im Inventar (nicht in Equipslots)
 *
 * @author Haus vom Nikolaus
 */
public class CTS_REQUEST_INV_ITEM_MOVE extends FixedSizeCTSCommand {

    public CTS_REQUEST_INV_ITEM_MOVE() {
        super(8);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int inventoryslot1 = Bits.getInt(data, 0);
        int inventoryslot2 = Bits.getInt(data, 4);
        client.getPlayer().moveInvItems(inventoryslot1, inventoryslot2);
    }

    /**
     * Tauscht 2 Items im Inventar
     * 2. Slot mus kein Item enthalten, dann wird nur 1. Item verschoben
     *
     * @param inventoryslot1 erster Inventarplatz
     * @param inventoryslot2 zweiter Inventarplatz
     */
    public static void sendInvItemMove(int inventoryslot1, int inventoryslot2) {
        byte b[] = new byte[8];
        Bits.putInt(b, 0, inventoryslot1);
        Bits.putInt(b, 4, inventoryslot2);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_INV_ITEM_MOVE, b));
    }
}
