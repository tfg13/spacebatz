package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Server setzt Unsichtbarkeits-Flag
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_SET_CHAR_INVISIBILITY extends FixedSizeSTCCommand {

    public STC_SET_CHAR_INVISIBILITY() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        Char c = GameClient.netIDMap.get(Bits.getInt(data, 0));
        if (c != null) {
            c.setInvisible(data[4] == 1);
        } else {
            System.out.println("ERROR: Cannot set visibility of unknown char (netid: " + Bits.getInt(data, 0) + ")");
        }
    }

    public static void broadcast(de._13ducks.spacebatz.server.data.entities.Char c) {
        byte[] data = new byte[5];
        Bits.putInt(data, 0, c.netID);
        data[4] = (byte) (c.isInvisible() ? 1 : 0);
        for (Client client : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_CHAR_INVISIBILITY, data), client);
        }
    }
}
