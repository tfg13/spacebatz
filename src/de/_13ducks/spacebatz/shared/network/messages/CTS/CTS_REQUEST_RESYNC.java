package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class CTS_REQUEST_RESYNC extends FixedSizeCTSCommand {

    public CTS_REQUEST_RESYNC() {
        super(0);
    }

    @Override
    public void execute(Client client, byte[] data) {
        Server.serverNetwork.udp.resyncClient(client);
    }

    public static void sendRequestResync() {
        // Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RESYNC, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_RESYNC, new byte[0]));

    }
}
