package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
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
        float targetX = decoder.readFloat();
        float targetY = decoder.readFloat();
        client.getPlayer().playerShoot(targetX, targetY);
    }

    /**
     * Sendet eine Schießen-Anforderung an den Server.
     *
     * @param targetX
     * @param targetY
     */
    public static void sendShoot(double targetX, double targetY) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeFloat((float) targetX);
        encoder.writeFloat((float) targetY);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_SHOOT, encoder.getBytes()));
    }
}
