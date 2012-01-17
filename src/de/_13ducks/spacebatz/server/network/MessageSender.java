package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Level;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Sendet Daten übers Netzwerk.
 * Kümmert sich darum, gescheite Pakete zu backen.
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MessageSender {

    /**
     * Sendet das Level an einen Client
     */
    public void sendLevel(Client client) {
        Server.serverNetwork.sendTcpData((byte) 20, Server.game.getSerializedLevel(), client);
        System.out.println("Level sent, length was: " + Server.game.getSerializedLevel().length);
    }
}
