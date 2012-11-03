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

import de._13ducks.spacebatz.Settings;

/**
 * Diese Klasse enthält stets aktuelle Statistikwerte, die für Netzwerk-Debugging verwendet werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class NetStats {

    /**
     * Ob der Net-Graph derzeit aktiv ist.
     */
    public static int netGraph = Settings.CLIENT_NETGRAPH_LEVEL;
    /**
     * Wieviele Pakete ankommen.
     */
    private static int inCounter = 0;
    /**
     * Wieviele Bytes ankommen.
     */
    private static int inBytes = 0;

    /**
     * Signalisieren, dass ein neues Paket angekommen ist.
     *
     * @param bytes wieviele Bytes angekommen sind.
     */
    public static void inPack(int bytes) {
        inCounter++;
        inBytes += bytes;
    }

    /**
     * Liefert den InCounter und setzt ihn zurück.
     *
     * @return den InCounter
     */
    public static int getAndResetInCounter() {
        int temp = inCounter;
        inCounter = 0;
        return temp;
    }

    /**
     * Liefert InBytes und setzt ihn zurück.
     *
     * @return InBytes
     */
    public static int getAndResetInBytes() {
        int temp = inBytes;
        inBytes = 0;
        return temp;
    }
    /**
     * Der Ping-Wert, wird vom Netzwerksystem ermittelt.
     */
    public static int ping;

    /**
     * Privater Konstruktor, da Utility-Class.
     */
    private NetStats() {
    }
}
