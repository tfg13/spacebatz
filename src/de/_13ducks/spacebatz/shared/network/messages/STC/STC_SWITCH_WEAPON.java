package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
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
public class STC_SWITCH_WEAPON extends FixedSizeSTCCommand {

    public STC_SWITCH_WEAPON() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        // Ein Client will andere Waffe auswählen
        int clientid = Bits.getInt(data, 0);
        byte wslot = data[4];
        if (clientid == GameClient.getClientID()) {
            GameClient.getPlayer().setSelectedattack(wslot);
        }
    }

    /**
     * Client wählt andere Waffe aus
     */
    public static void sendWeaponswitch(Client client, byte slot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, client.clientID);
        b[4] = slot;
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_SWITCH_WEAPON, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_SWITCH_WEAPON, b), c);

        }
    }
}
