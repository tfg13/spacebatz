package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Spieler will feuern.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_SHOOT extends FixedSizeCTSCommand {

    public CTS_SHOOT() {
        super(8);
    }

    @Override
    public void execute(Client client, byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        float dir = decoder.readFloat();
        float distance = decoder.readFloat();
        double x = client.getPlayer().getX() + distance * Math.cos(dir);
        double y = client.getPlayer().getY() + distance * Math.sin(dir);
        client.getPlayer().playerShoot(dir);
    }

    /**
     * Sendet eine Schießen-Anforderung an den Server.
     *
     * @param dx
     * @param dy
     * @param distance
     */
    public static void sendShoot(double dir, float distance) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeFloat((float) dir);
        encoder.writeFloat(distance);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_SHOOT, encoder.getBytes()));
    }
}
