/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.main;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.Server;
import java.net.UnknownHostException;

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
                Server.startServer();
            }
        });
        // Client
        Thread client = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GameClient.startClient("127.0.0.1");
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
            }
        });
        // Server starten
        server.start();
        // Server zeit geben zum Socket aufmachen
        Thread.sleep(5000);
        // Client starten & verbinden lassen:
        client.start();

    }
}
