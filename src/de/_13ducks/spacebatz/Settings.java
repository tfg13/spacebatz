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
package de._13ducks.spacebatz;

/**
 * Spieleinstellungen, der Einfachheit halber hier statisch erreichbar. Achtung: Für Server und Client gleich!!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class Settings {

   

    /**
     * Private, da Utility-Class
     */
    private Settings() {
    }
    
    /**
     * Die X-Auflösung der Grafikausgabe.
     */
    public static final int CLIENT_GFX_RES_X = 800;
    /**
     * Die Y-Auflösung der Grafikausgabe.
     */
    public static final int CLIENT_GFX_RES_Y = 640;
    /**
     * Die Größe eines Tiles der Tilemaps in Pixeln.
     */
    public static final int CLIENT_GFX_TILESIZE = 16;
    /**
     * Der ganzzahlige Zoomfaktor. Für Indie-Pixelartlook mindestens 2 verwenden!
     */
    public static final int CLIENT_GFX_TILEZOOM = 2;
    /**
     * Frames auf dieses Anzahl limitieren. Dingend empfohlen. Hinweis: Hier wirkt auch vsync rein. Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static final int CLIENT_GFX_FRAMELIMIT = 60;
    /**
     * VSync-Option. Empfohlen, macht Bildschirmausgabe viel flüssiger. Beeinflusst die Framerate direkt (double-buffering). Auf den meisten Systemen wird die Framerate durch diese Option 60. Hinweis:
     * Die Framerate wird auch von FRAMELIMIT beeinfluss. Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static final boolean CLIENT_GFX_VSYNC = true;
    /**
     * Der Port auf dem der Server auf RCON-Anfragen lauscht, falls überhaupt erlaubt. Clients dürfen sich dahin aber nicht einfach so verbinden.
     */
    public static final int SERVER_RCONPORT = 13946;
    /**
     * Der primäre UDP-Port des neuen Server-Netzwerksystems. Das neue System benötigt keine festen Ports beim Client mehr.
     */
    public static final int SERVER_UDPPORT2 = 13949;
    /**
     * Die Server-Tickrate in default-Delay zwischen Ticks. 15 entspricht also einer Tickrate von 66,66666666...
     * Wert darf nicht negativ sein, also nicht über 127 steigen. Die minimal mögliche Server-Tickrate ist also etwa 8 Ticks/s
     */
    public static final byte SERVER_TICKRATE = 15;
    /**
     * Ob der Server eingehende rcon-Verbindungen akzeptiert.
     */
    public static final boolean SERVER_ENABLE_RCON = true;
    /**
     * Maximale Anzahl Spieler gleichzeitig auf einem Server. Darf ohne Änderungen am Code 127 nicht übersteigen!
     */
    public static final int SERVER_MAXPLAYERS = 32;
    /**
     * Größe des Inventar jedes Spielers
     */
    public static final int INVENTORY_SIZE = 96;
    /**
     * Die Reichweite für Kollisionen
     */
    public static final double SERVER_COLLISION_DISTANCE = 1.5;
    /**
     * Wie häufig der Spawner pro Sekunde aufgerufen wird.
     */
    public static final int SERVER_SPAWNER_EXECSPERSEC = 5;
    /**
     * Die Größe der Chars für Kollision
     */
    public static final double CHARSIZE = 1.8;
    /**
     * Die HP der Chars
     */
    public static final int CHARHEALTH = 10;
    /**
     * Default-Level des netGraphen. 0 - Aus 1 - + Netzwerkdebug 2 - + GameDebug
     */
    public static final int CLIENT_NETGRAPH_LEVEL = 2;
    /**
     * Abstand bei der double noch als "gleich" gilt.
     */
    public static final double DOUBLE_EQUALS_DIST = 0.0001;
    /**
     * Grundgeschwindigkeit.
     */
    public static final double BASE_MOVESPEED = 0.15;
    /**
     * Anzahl der Materialien im Spiel (Geld, Erze, ...)
     */
    public static final int NUMBER_OF_MATERIALS = 3;
}
