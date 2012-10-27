package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class CTS_EQUIP_ITEM extends FixedSizeCTSCommand {

    public CTS_EQUIP_ITEM() {
        super(5);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int inventoryslot = Bits.getInt(data, 0);
        byte equipslot = data[4];
        client.getPlayer().equipItem(inventoryslot, equipslot);
    }

    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     */
    public static void sendEquipItem(int inventoryslot, byte equipslot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, inventoryslot);
        b[4] = equipslot;
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_EQUIP, b));
    }
}
