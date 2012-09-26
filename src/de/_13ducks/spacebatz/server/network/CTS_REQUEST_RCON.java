package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.network.STC_ANSWER_RCON;
import de._13ducks.spacebatz.server.data.Client;

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
}
