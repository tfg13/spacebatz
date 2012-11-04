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
 * Diese Klasse hat ihre besten Tage hinter sich und wird entweder bald gelöscht oder sie bekommt wieder mehr Aufgaben.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class NetStats {

    /**
     * Ob der Net-Graph derzeit aktiv ist.
     */
    public static int netGraph = Settings.CLIENT_NETGRAPH_LEVEL;
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
