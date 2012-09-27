package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Spieler will feuern.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_SHOOT extends FixedSizeCTSCommand {
    
    public CTS_SHOOT() {
        super(4);
    }

    @Override
    public void execute(Client client, byte[] data) {
         client.getPlayer().playerShoot(Bits.getFloat(data, 0));
    }
    
    public static void sendShoot(byte[] data) {
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_CTS_SHOOT, data));
    }

}
