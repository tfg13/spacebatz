package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;

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
        System.out.println("Sending LEvel to Clouietn.");

        // Level in 100-Byte-Blöcken senden:%
        int send = 0;
        int index = 0;
        byte buffer[] = new byte[100];
        while (true) {
            send = 100;
            if (Server.game.getSerializedLevel().length - index < 100) {
                send = (Server.game.getSerializedLevel().length - index);
            }
            for (int i = 0; i < send; i++) {
                buffer[i] = Server.game.getSerializedLevel()[i];
            }
            for (int i = 0; i < send; i++) {
                Server.serverNetwork.sendData(buffer, client);
            }
            index += send;
            if (index == Server.game.getSerializedLevel().length) {
                break;
            }

        }



        System.out.println("Level sent.");
    }
}
