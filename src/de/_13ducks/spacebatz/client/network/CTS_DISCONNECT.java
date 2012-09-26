package de._13ducks.spacebatz.client.network;

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
public class CTS_DISCONNECT extends FixedSizeCTSCommand {

    public CTS_DISCONNECT() {
        super(0);
    }

    @Override
    public void execute(Client client, byte[] data) {
        Server.disconnectClient(client);
    }

    public static void sendDisconnect() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[0]));

    }
}
