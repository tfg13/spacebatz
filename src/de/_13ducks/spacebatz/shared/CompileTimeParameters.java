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
package de._13ducks.spacebatz.shared;

/**
 * Parameter, die feststehen und normalerweise nicht geändert werden müssen.
 * Im Gegensatz zu dem Settings sind diese hier final und können deshalb ins
 * Ziel direkt eincompiliert werden.
 * Dafür lassen diese Werte sich aber nicht so einfach wie Settings überschreiben.
 * 
 * Faustregel für Trennung zwischen DefaultSettings und CompileTimeParameters:
 * Werte, die bei mehreren Clients auf dem gleichen Server konstant sein müssen hier rein.
 *
 * @author tfg
 */
public final class CompileTimeParameters {
    
    /**
     * Privater Konstruktor, da Utility-Class.
     */
    private CompileTimeParameters() {
    }
    
    /**
     * Die Server-Tickrate in default-Delay zwischen Ticks. 15 entspricht also einer Tickrate von 66,66666666... Wert darf nicht negativ sein, also nicht über 127 steigen. Die minimal mögliche
     * Server-Tickrate ist also etwa 8 Ticks/s
     */
    public static final byte SERVER_TICKRATE = 15;
    /**
     * Größe des Inventar jedes Spielers
     */
    public static final int INVENTORY_SIZE = 30;
    /**
     * Die Reichweite für Kollisionen
     */
    public static final double SERVER_COLLISION_DISTANCE = 1.5;
    /**
     * Die Größe der Chars für Kollision
     */
    public static final double CHARSIZE = 1.8;
    /**
     * Die Größe der Bullets für Kollision
     */
    public static final double BULLETSIZE = 0.1;
    /**
     * Die HP der Chars
     */
    public static final int CHARHEALTH = 100;
    /**
     * Abstand bei der double noch als "gleich" gilt.
     */
    public static final double DOUBLE_EQUALS_DIST = 0.0001;
    /**
     * Grundgeschwindigkeit.
     */
    public static final double BASE_MOVESPEED = 0.15;
    /**
     * Zeit, bis Spieler nach seinem Tod respawnt, in Ticks
     */
    public static final int RESPAWNTIME = 300;
    /**
     * Wie viele Materialen es gibt.
     */
    public static final int NUMBER_OF_MATERIALS = 3;
    /**
     * Maximal zulässiger Unterschied zwischen Logik-Tickzählung des Clients,
     * und Originaltick des letzten verarbeiteten Pakets.
     * Bei größeren Werten wird der strengere Pakettick bevorzugt, bis es sich wieder normalisiert.
     */
    public static final int CLIENT_NET_ACCEPTABLE_LOGIC_DELTA = 4;
    /**
     * Maximal zulässige Abweichung der vorhergesagten Spielerposition mit der vom Server empfangenen.
     * Angabe in Feldern pro Tick eingestelltem Lerp.
     * Bei einem Wert von 0.5 und Lerp 10 darf die Position also bis zu 5 Felder abweichen.
     */
    public static final double CLIENT_PREDICT_MAX_DELTA_PER_LERP = 0.2;
    
}
