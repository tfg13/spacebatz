package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.shared.DefaultSettings;
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
public class CTS_REQUEST_ITEM_DEQUIP extends FixedSizeCTSCommand {

    public CTS_REQUEST_ITEM_DEQUIP() {
        super(5);
    }

    @Override
    public void execute(Client client, byte[] data) {
        int slottype = Bits.getInt(data, 0);
        byte selslot = data[4];
        client.getPlayer().clientDequipItem(slottype, selslot);
    }

    /**
     * Client will Item ablegen, muss dafür aber erst Server fragen
     */
    public static void sendDequipItem(int slottype, byte selslot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, slottype);
        b[4] = selslot;
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, b);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, b));

    }
}
