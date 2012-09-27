package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

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
        BitDecoder decoder = new BitDecoder(data);
        float dir = decoder.readFloat();
        client.getPlayer().playerShoot(dir);
    }

    public static void sendShoot(double dx, double dy) {
        BitEncoder encoder = new BitEncoder();
        double dir = Math.atan2(dy, dx);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        encoder.writeFloat((float) dir);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_CTS_SHOOT, encoder.getBytes()));
    }
}
