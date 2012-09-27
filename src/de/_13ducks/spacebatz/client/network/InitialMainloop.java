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
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;

/**
 * Temporärer Mainloop der solange läuft, bis die Engine startet.
 * Ruft einfach Network.inTick() und Network.outTick() in Endlosschleigfe auf.
 *
 * @author michael
 */
public class InitialMainloop {

    /**
     * Thread der die Netzwerkkommunikation übernimmt.
     */
    private Thread initMainloop;
    /**
     * gibt an, ob der initTcpReceiverThread noch benötigt wird.
     */
    private boolean initMainloopRun = true;

    /**
     * Initialisiert den InitMainloop
     */
    public InitialMainloop() {
        // Der Thread der anfangs TcpPackete empfängt:
        initMainloop = new Thread(new Runnable() {
            @Override
            public void run() {
                while (initMainloopRun) {
                    try {
                        GameClient.getNetwork2().inTick();
                        GameClient.getNetwork2().outTick();
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        initMainloop.setName("initialTcpReceiverThread");
        initMainloop.setDaemon(true);
    }

    /**
     * Stoppt den InitialMainloop, so dass der der Engine übernehmen kann.
     */
    public void start() {
        initMainloopRun = false;
    }

    /**
     * Startet den InitialMainloop.
     */
    public void stop() {
        initMainloopRun = true;
        initMainloop.start();
    }
}
