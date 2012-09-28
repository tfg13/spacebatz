package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_MOVE extends FixedSizeCTSCommand {
    
    public CTS_MOVE() {
        super(1);
    }

    @Override
    public void execute(Client client, byte[] data) {
        client.getPlayer().clientMove((data[0] & 0x80) != 0, (data[0] & 0x40) != 0, (data[0] & 0x20) != 0, (data[0] & 0x10) != 0);
    }
    
    public static void sendMove(byte dir) {
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_MOVE, new byte[]{dir}));
    }

}
