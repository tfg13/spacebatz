package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SET_CLIENT extends FixedSizeSTCCommand {

    public STC_SET_CLIENT() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        // ClientID setzen
        //GameClient.setClientID(data[0]);
    }

    /**
     * Setzt die ClientID eines Clients auf dem Client.
     *
     * @param client der Client, dessen ID an ihn gesendet wird.
     */
    public static void sendSetClientID(Client client) {
        //Server.serverNetwork.sendTcpData((byte) MessageIDs.NET_STC_SET_CLIENT, new byte[]{(byte) client.clientID}, client);
        byte[] data = new byte[5];
        data[0] = client.clientID;
        Bits.putInt(data, 1, client.getPlayer().netID);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_CLIENT, data), client);
    }
}
