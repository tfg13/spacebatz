package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.SpacebatzServer;
import de._13ducks.spacebatz.server.data.Char;

/**
 * Erstellt und Sendet Nachrichten an die Clients
 * @author michael
 */
public class MessageSender {

    /**
     * Broadcasts the Positon of a char to all clients
     * @param c the char
     */
    public void broadcastCharPosition(Char c) {
        byte message[] = {3, (byte) c.id, (byte) c.posX, (byte) c.posY};
        SpacebatzServer.serverNetwork.broadcastData(message);
    }
}
