package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_MOVE extends FixedSizeCTSCommand {
    
    public CTS_MOVE() {
        super(12);
    }

    @Override
    public void execute(Client client, byte[] data) {
        client.getPlayer().clientMove(Bits.getFloat(data, 0), Bits.getFloat(data, 4), Bits.getFloat(data, 8));
    }
    
    public static void sendMove(float turretDir, float targetX, float targetY) {
        byte[] data = new byte[12];
        Bits.putFloat(data, 0, turretDir);
        Bits.putFloat(data, 4, targetX);
        Bits.putFloat(data, 8, targetY);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_MOVE, data));
    }

}
