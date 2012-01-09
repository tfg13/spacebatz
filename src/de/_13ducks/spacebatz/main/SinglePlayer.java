package de._13ducks.spacebatz.main;

import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.server.Server;

/**
 * Enthält main für SinglePlayer, also ein Server, ein verbundener Client.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class SinglePlayer {

    public static void main(String[] args) throws InterruptedException {
        // Server
        Thread server = new Thread(new Runnable() {

            @Override
            public void run() {
                Server.main(new String[]{});
            }
        });
        // Client
        Thread client = new Thread(new Runnable() {

            @Override
            public void run() {
                Client.main(new String[]{});
            }
        });
        // Server starten
        server.start();
        // Server zeit geben zum Socket aufmachen
        Thread.sleep(500);
        // Client starten & verbinden lassen:
        client.start();
    }
}
