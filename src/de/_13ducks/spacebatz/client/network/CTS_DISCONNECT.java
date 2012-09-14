package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;

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
}
