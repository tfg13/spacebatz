package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Eine Client-Anfrage zur Tick-Synchonisation.
 * Muss vom Server mit STC_TICK_SYNC beantwortet werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class CTS_TICK_SYNC extends CTSCommand {

    @Override
    public void execute(Client client, byte[] data) {
        byte[] ansData = new byte[4];
        Bits.putInt(ansData, 0, Server.game.getTick());
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TICK_SYNC, ansData), client);
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 0;
    }
}
