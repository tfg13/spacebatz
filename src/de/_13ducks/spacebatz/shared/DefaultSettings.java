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
 * Spieleinstellungen, der Einfachheit halber hier statisch erreichbar. Achtung: Für Server und Client gleich!!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DefaultSettings {

    /**
     * Private, da Utility-Class
     */
    private DefaultSettings() {
    }
    /**
     * Die X-Auflösung der Grafikausgabe.
     */
    public static int CLIENT_GFX_RES_X = 1280;
    /**
     * Die Y-Auflösung der Grafikausgabe.
     */
    public static int CLIENT_GFX_RES_Y = 720;
    /**
     * Die Größe eines Tiles der Tilemaps in Pixeln.
     */
    public static int CLIENT_GFX_TILESIZE = 16;
    /**
     * Frames auf dieses Anzahl limitieren. Dingend empfohlen. Hinweis: Hier wirkt auch vsync rein. Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static int CLIENT_GFX_FRAMELIMIT = 60;
    /**
     * VSync-Option. Empfohlen, macht Bildschirmausgabe viel flüssiger. Beeinflusst die Framerate direkt (double-buffering). Auf den meisten Systemen wird die Framerate durch diese Option 60. Hinweis:
     * Die Framerate wird auch von FRAMELIMIT beeinfluss. Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static boolean CLIENT_GFX_VSYNC = true;
    /**
     * Die Einstellung für die Schatten. Es gibt 0 - aus (Cheat!), 1 - block, 2 - smooth, 3 - shader.
     * Die höchste Zahl bietet die beste Qualität, kostet aber am meisten Leistung.
     * Mindestens 2 ist empfohlen.
     */
    public static int CLIENT_GFX_SHADOWLEVEL = 3;
    /**
     * Die Einstellungen für das Zeichnen der Berge.
     * Wenn true, werden diese am Rand abgerundet und mit einer Kontur versehen.
     * Empfohlen.
     */
    public static boolean CLIENT_GFX_TOP_FANCY = true;
    /**
     * Die Einstellung für das Zeichen von Übergängen auf dem Boden.
     * Wenn true, wird an Texturgrenzen übergeblendet (alpha-blending).
     * Empfohlen.
     */
    public static boolean CLIENT_GFX_GROUND_SMOOTH = true;
    /**
     * Die Einstellung für den Lookahead-Sichtmodus.
     * Bei diesem ist die Einheit nicht immer in der Mitte, sondern die Position ist abhängig von der Maus.
     * Muss jeder ausprobieren, ob er das mag.
     * Alles ist in diesem Modus stärker reingezoomt, man sieht also insbesondere bei kleinen Auflösungen mehr Details.
     */
    public static boolean CLIENT_GFX_LOOKAHEAD = false;
    /**
     * Der Port auf dem der Server auf RCON-Anfragen lauscht, falls überhaupt erlaubt. Clients dürfen sich dahin aber nicht einfach so verbinden.
     */
    public static int SERVER_RCONPORT = 13946;
    /**
     * Der primäre UDP-Port des neuen Server-Netzwerksystems. Das neue System benötigt keine festen Ports beim Client mehr.
     */
    public static int SERVER_UDPPORT2 = 13949;
    /**
     * Die Server-Tickrate in default-Delay zwischen Ticks. 15 entspricht also einer Tickrate von 66,66666666... Wert darf nicht negativ sein, also nicht über 127 steigen. Die minimal mögliche
     * Server-Tickrate ist also etwa 8 Ticks/s
     */
    public static byte SERVER_TICKRATE = 15;
    /**
     * Ob der Server eingehende rcon-Verbindungen akzeptiert.
     */
    public static boolean SERVER_ENABLE_RCON = true;
    /**
     * Maximale Anzahl Spieler gleichzeitig auf einem Server. Darf ohne Änderungen am Code 127 nicht übersteigen!
     */
    public static int SERVER_MAXPLAYERS = 32;
    /**
     * Wieviele Pakete à 1500 Bytes maximal pro Tick zu einem Client verschickt werden können. Das ist eine Beschränkung des Peak-Werts für einzelne Ticks, es wird nicht erwartet, das Netzwerk über
     * längere Zeit so viele Pakete pro Tick verträgt.
     */
    public static int SERVER_MAXPACKPERCLIENT = 10;
    /**
     * Größe des Inventar jedes Spielers
     */
    public static int INVENTORY_SIZE = 30;
    /**
     * Die Reichweite für Kollisionen
     */
    public static double SERVER_COLLISION_DISTANCE = 1.5;
    /**
     * Wie häufig der Spawner pro Sekunde aufgerufen wird.
     */
    public static int SERVER_SPAWNER_EXECSPERSEC = 5;
    /**
     * Die Größe der Chars für Kollision
     */
    public static double CHARSIZE = 1.8;
    /**
     * Die Größe der Bullets für Kollision
     */
    public static double BULLETSIZE = 0.1;
    /**
     * Die HP der Chars
     */
    public static int CHARHEALTH = 100;
    /**
     * Animierte Drehgeschwindigkeit auf dem Client.
     */
    public static double CHAR_TURN_SPEED = 0.30;
    /**
     * Default-Level des netGraphen. 0 - Aus 1 - + Netzwerkdebug 2 - + GameDebug 3 - + Server-Netzdebug
     */
    public static int CLIENT_NETGRAPH_LEVEL = 3;
    /**
     * Abstand bei der double noch als "gleich" gilt.
     */
    public static double DOUBLE_EQUALS_DIST = 0.0001;
    /**
     * Grundgeschwindigkeit.
     */
    public static double BASE_MOVESPEED = 0.15;
    /**
     * Anzahl der Materialien im Spiel (Geld, Erze, ...)
     */
    public static int NUMBER_OF_MATERIALS = 3;
    /**
     * Zeit, bis Spieler nach seinem Tod respawnt, in Ticks
     */
    public static int RESPAWNTIME = 300;
    /**
     * Alle wieviel Ticks die Turret-Drehung eines Spielers gebroadcastet wird.
     * Für Leitungen mit geringerem Durchsatz höher einstellen.
     */
    public static int TURRET_DIR_UPDATE_INTERVAL = 1;
    /**
     * Alle wieviel Ticks der Server Netzwerkstatistiken mit dem Client abgleicht.
     */
    public static int SERVER_STATS_INTERVAL = 66;

    /*
     * DAS MUSS IMMER GANZ UNTEN SEIN!!!
     */
    static {
        SettingsLoader.overrideValues();
    }
    /*
     * HIER AUF KEINEN FALL ETWAS EINFÜGEN!!!
     */
}
