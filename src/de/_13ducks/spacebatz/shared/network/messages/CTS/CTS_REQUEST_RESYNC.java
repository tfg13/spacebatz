package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;

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
}
