package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Ein Client verlässt den Server.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_DEL_CLIENT extends FixedSizeSTCCommand {

    public STC_DEL_CLIENT() {
        super(1);
    }

    @Override
    public void execute(byte[] data) {
        int clientID = data[0];
        if (GameClient.players.containsKey(clientID)) {
            GameClient.players.remove(clientID);
        } else {
            System.out.println("WARN: CNET: SYNC: Cannot remove Client " + clientID + ", target does not exist!");
        }
    }

    /**
     * Sendet an alle außer dem gegebenen eine Nachricht, dass der gegebene raus fliegt.
     *
     * @param delClient der zu löschende Client
     */
    public static void broadcast(Client delClient) {
        for (Client c : Server.game.clients.values()) {
            if (c.equals(delClient)) {
                // Der, der gelöscht wird, muss darüber nicht nochmal informiert werden.
                continue;
            }
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_DEL_CLIENT, new byte[]{delClient.clientID}), c);
        }
    }
}
