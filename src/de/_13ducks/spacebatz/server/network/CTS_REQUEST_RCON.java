package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STC_ANSWER_RCON;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class CTS_REQUEST_RCON extends FixedSizeCTSCommand {

    public CTS_REQUEST_RCON() {
        super(0);
    }

    @Override
    public void execute(Client client, byte[] data) {
        STC_ANSWER_RCON.sendRconAnswer(client, Settings.SERVER_ENABLE_RCON, Settings.SERVER_RCONPORT);
    }

    public static void sendRconRequest() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RCON, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_REQUEST_RCON, new byte[0]));

    }
}
