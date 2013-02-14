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
        super(5);
    }

    @Override
    public void execute(Client client, byte[] data) {
        client.getPlayer().clientMove((data[0] & 0x80) != 0, (data[0] & 0x40) != 0, (data[0] & 0x20) != 0, (data[0] & 0x10) != 0, Bits.getFloat(data, 1));
    }
    
    public static void sendMove(byte dir, float turretDir) {
        byte[] data = new byte[5];
        data[0] = dir;
        Bits.putFloat(data, 1, turretDir);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_MOVE, data));
    }

}
