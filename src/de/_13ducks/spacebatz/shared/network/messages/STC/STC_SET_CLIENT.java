package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class STC_SET_CLIENT extends FixedSizeSTCCommand {

    public STC_SET_CLIENT() {
        super(1);
    }

    @Override
    public void execute(byte[] data) {
        // ClientID setzen
        GameClient.setClientID(data[0]);
    }

    /**
     * Setzt die ClientID eines Clients auf dem Client.
     *
     * @param client der Client, dessen ID an ihn gesendet wird.
     */
    public static void sendSetClientID(Client client) {
        //Server.serverNetwork.sendTcpData((byte) MessageIDs.NET_STC_SET_CLIENT, new byte[]{(byte) client.clientID}, client);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_CLIENT, new byte[]{(byte) client.clientID}), client);
    }
}
