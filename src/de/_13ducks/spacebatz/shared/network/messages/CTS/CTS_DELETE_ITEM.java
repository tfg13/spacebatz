package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * 
 * @author Johannes
 *
 */
public class CTS_DELETE_ITEM extends FixedSizeCTSCommand {

	public CTS_DELETE_ITEM() {
		super(4);
	}

	@Override
	public void execute(Client client, byte[] data) {
        int inventoryslot = Bits.getInt(data, 0);
        client.getPlayer().deleteItem(inventoryslot);
	}
	
	/**
	 * Client will Item löschen
	 * @param slot welcher Inventarslot
	 */
	public static void sendDeleteItem(int inventoryslot) {
        byte[] b = new byte[4];
        Bits.putInt(b, 0, inventoryslot);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_INV_ITEM_DELETE, b));
	}

}
