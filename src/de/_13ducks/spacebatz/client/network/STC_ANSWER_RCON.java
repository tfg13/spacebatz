package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_ANSWER_RCON extends FixedSizeSTCCommand {

    public STC_ANSWER_RCON() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        if (data[0] == 1) {
            // Server erlaubt rcon
            GameClient.terminal.rcon(Bits.getInt(data, 1));
        }
    }

    /**
     * Antwortet dem Client mit ja oder nein auf seine rcon-anfrage
     *
     * @param sender
     * @param answer
     * @param port
     */
    public static void sendRconAnswer(Client sender, boolean answer, int port) {
        byte[] b = new byte[5];
        b[0] = (byte) (answer ? 1 : 0);
        Bits.putInt(b, 1, port);
        //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_ANSWER_RCON, b, sender);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_ANSWER_RCON, b), sender);

    }
}
