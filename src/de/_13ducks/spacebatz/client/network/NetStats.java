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

import de._13ducks.spacebatz.shared.DefaultSettings;

/**
 * Diese Klasse enthält stets aktuelle Statistikwerte, die für Netzwerk-Debugging verwendet werden.
 * Die meisten Werte in dieser Klasse werden vom Server gesendet und nicht selbst ermittelt.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class NetStats {

    /**
     * Ob der Net-Graph derzeit aktiv ist.
     */
    public static int netGraph = DefaultSettings.CLIENT_NETGRAPH_LEVEL;
    /**
     * Der Ping-Wert, wird vom Netzwerksystem ermittelt.
     */
    public static int ping;
    /**
     * Die durchschnittliche Anzahl von Befehlen pro Netzwerkpaket vom Server.
     */
    public static double avgNumberOfCmdsPerPacket;
    /**
     * Die durchschnittliche Anzahl von Prio-Befehlen pro Netzwerkpaket vom Server.
     */
    public static double avgNumberOfPrioCmdsPerPacket;
    /**
     * Die durchschnittliche Auslastung eines Netzwerkpakets vom Server.
     */
    public static double avgLoadPerPacket;
    /**
     * Die aktuelle Anzahl von Paketen pro Tick.
     */
    public static double recentNumberOfPacketsPerTick;
    /**
     * Die aktuelle Auslastung des Server-Outbuffers.
     */
    public static double recentOutBufferLoad;
    /**
     * Die aktuelle Größe der Server-Outqueue.
     */
    public static double recentOutQueueSize;
    /**
     * Die aktuelle Größe der Server-Prioqueue.
     */
    public static double recentPrioOutQueueSize;
    /**
     * Die aktuelle Rate an Paketen, die erneut versendet werden müssen.
     */
    public static double recentRetransmitNumber;

    /**
     * Privater Konstruktor, da Utility-Class.
     */
    private NetStats() {
    }
}
