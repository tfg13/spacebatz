package de._13ducks.spacebatz.shared.network.messages.STC;

import java.util.Arrays;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

public class STC_DELETE_ITEM extends FixedSizeSTCCommand {

	public STC_DELETE_ITEM() {
		super(8);
	}

	@Override
	public void execute(byte[] data) {
        int inventoryslot = Bits.getInt(data, 0);
        int clientID = Bits.getInt(data, 4); // clientID des Spielers

        if (clientID == GameClient.getClientID()) {
        	if (inventoryslot == -1) {
        		// gesamtes Inventar löschen
        		Arrays.fill(GameClient.getItems(), null);
        	} else {
        		// einzelnes Item löschen
        		GameClient.getItems()[inventoryslot] = null;
        	}
        }
	}
	
	/**
	 * Item aus Inventar löschen
	 */
	public static void sendItemDelete(int inventoryslot, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[8];
            Bits.putInt(b, 0, inventoryslot);
            Bits.putInt(b, 4, clientID);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_INV_ITEM_DELETE, b), c);
        }
	}

}
