package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class CTS_EQUIP_ITEM extends FixedSizeCTSCommand {

    public CTS_EQUIP_ITEM() {
        super(9);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int netID = Bits.getInt(data, 0);
        byte selectedslot = data[4];
        client.getPlayer().clientEquipItem(netID, selectedslot);
    }

    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     */
    public static void sendEquipItem(Item item, byte selectedslot) {
        byte[] b = new byte[9];
        Bits.putInt(b, 0, item.getNetID());
        b[4] = selectedslot;
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_ITEM_EQUIP, b);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_EQUIP, b));
    }
}
