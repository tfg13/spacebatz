package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class CTS_REQUEST_SWITCH_WEAPON extends FixedSizeCTSCommand {

    public CTS_REQUEST_SWITCH_WEAPON() {
        super(1);
    }

    @Override
    public void execute(Client client, byte[] data) {
        byte selslot2 = data[0];
        client.getPlayer().clientSetSelectedSlot(selslot2);
    }

    /**
     * Client will andere Waffe auswählen
     */
    public static void sendSwitchWeapon(byte slot) {
        byte[] b = new byte[1];
        b[0] = slot;
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH, b);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_WEAPONSWITCH, b));
    }
}
