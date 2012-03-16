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
     * Frames auf dieses Anzahl limitieren. Dingend empfohlen. Hinweis: Hier wirkt auch vsync rein. Die tatsächliche max-Framerate ist immer
     * min(framelimit, vsync)
     */
    public static final int CLIENT_GFX_FRAMELIMIT = 60;
    /**
     * VSync-Option. Empfohlen, macht Bildschirmausgabe viel flüssiger. Beeinflusst die Framerate direkt (double-buffering). Auf den meisten Systemen
     * wird die Framerate durch diese Option 60. Hinweis: Die Framerate wird auch von FRAMELIMIT beeinfluss. Die tatsächliche max-Framerate ist immer
     * min(framelimit, vsync)
     */
    public static final boolean CLIENT_GFX_VSYNC = true;
    /**
     * Der Port auf dem der Server auf TCP-Verbindugen wartet.
     */
    public static final int SERVER_TCPPORT = 10000;
    /**
     * Der Port auf dem der Server auf RCON-Anfragen lauscht, falls überhaupt erlaubt.
     * Clients dürfen sich dahin aber nicht einfach so verbinden.
     */
    public static final int SERVER_RCONPORT = 13946;
    /**
     * Auf diesem Port ist der Server per UDP zu erreichen. Darf nicht der gleiche sein, wie CLIENT_UDPPORT
     */
    public static final int SERVER_UDPPORT = 13947;
    /**
     * Auf diesem Port ist der Client per UDP zu erreichen. Darf nicht der gleiche sein, wie SERVER_UDPPORT
     */
    public static final int CLIENT_UDPPORT = 13948;
    /**
     * Die Server-Tickrate in default-Delay zwischen Ticks.
     * 15 entspricht also einer Tickrate von 66,66666666...
     */
    public static final int SERVER_TICKRATE = 15;
    /**
     * Ob der Server eingehende rcon-Verbindungen akzeptiert.
     */
    public static final boolean SERVER_ENABLE_RCON = true;
    /**
     * Wieviel der Server maximal hinter seiner Target-Tickrate sein darf, bevor eine Warnung ausgespuckt wird.
     */
    public static final int SERVER_TICKMISS_MAX_NANOS = 125000000;
    /**
     * Größe des Empfangspuffers für ein Clientpacket auf Serverseite. NICHT EINFACH VERÄNDERN!
     */
    public static final int NET_UDP_CTS_SIZE = 10;
    /**
     * Größe des Empfangspuffers für ein Serverpacket auf Clientseite. NICHT EINFACH VERÄNDERN!
     */
    public static final int NET_UDP_STC_MAX_SIZE = 512;
    /**
     * Normales Einheitenupdate, das Regelmäßig verschickt wird.
     */
    public static final byte NET_UDP_CMD_NORMAL_ENTITY_UPDATE = 10;
    /**
     * Char einfügen.
     */
    public static final byte NET_UDP_CMD_ADD_ENTITY = 12;
    /**
     * Char löschen.
     */
    public static final byte NET_UDP_CMD_DEL_ENTITY = 13;
    /**
     * Bestätigung, dass eine Bewegung vom Server beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_MOVE = -12;
    /**
     * Bestätigung, dass das Erstellen einer Einheit beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_ADD_ENTITY = -13;
    /**
     * Bestätigung, dass das Löschen einer Einheit beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_DEL_ENTITY = -14;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_INPUT = -10;
    /**
     * Ping-Request an den Server.
     */
    public static final byte NET_UDP_CMD_PING = -5;
    /**
     * Ping-Antwort vom Server.
     */
    public static final byte NET_UDP_CMD_PONG = 5;
    /**
     * Server schickt Infos über Tickdelay.
     */
    public static final byte NET_UDP_CMD_TICK_SYNC_PING = 1;
    /**
     * Antwort an Server, dass Tickdelay-Paket erhalten wurde.
     */
    public static final byte NET_UDP_CMD_TICK_SYNC_PONG = -1;
    /**
     * Die cmdID für Level-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_LEVEL = 20;
    /**
     * Die cmdID für Char mit Angriff / Bullet treffen
     */
    public static final byte NET_TCP_CMD_CHAR_HIT = 28;
    /**
     * Die cmdID für EnemyTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_ENEMYTYPES = 29;
    /**
     * Die cmdID für EnemyTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_BULLETTYPES = 30;
    /**
     * Die cmdID für ItemTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_ITEMTYPES = 31;
    /**
     * Die cmdID für Item-Drop senden
     */
    public static final byte NET_TCP_CMD_SPAWN_ITEM = 32;
    /**
     * Die cmdID für Item-Aufsammeln senden
     */
    public static final byte NET_TCP_CMD_GRAB_ITEM = 33;
    /**
     * Die cmdID für komplette Liste der herumliegenden Items
     */
    public static final byte NET_TCP_CMD_TRANSFER_ITEMS = 34;
    /**
     * Die cmdID für geänderten Boden
     */
    public static final byte NET_TCP_CMD_CHANGE_GROUND = 35;
    /**
     * Die cmdId für Kollisionsänderung
     */
    public static final byte NET_TCP_CMD_CHANGE_COLLISION = 44;
    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     */
    public static final byte NET_TCP_CMD_REQUEST_ITEM_EQUIP = 36;
    /**
     * Client will Item ablegen, muss dafür aber erst Server fragen
     */
    public static final byte NET_TCP_CMD_REQUEST_ITEM_DEQUIP = 37;
    /**
     * Server sagt, dass Client Item anzieht
     */
    public static final byte NET_TCP_CMD_EQUIP_ITEM = 38;
    /**
     * Server sagt, dass Client Item auszieht
     */
    public static final byte NET_TCP_CMD_DEQUIP_ITEM = 39;
    /**
     * Client geht offline und meldet sich ab.
     */
    public static final byte NET_TCP_CMD_CLIENT_DISCONNECT = 40;
    /**
     * Client will andere Waffe auswählen
     */
    public static final byte NET_TCP_CMD_REQUEST_WEAPONSWITCH = 41;
    /**
     * Server ändert für einen Client die gerade ausgewählte Waffe
     */
    public static final byte NET_TCP_CMD_SWITCH_WEAPON = 42;
    /**
     * Client will resync.
     */
    public static final byte NET_TCP_CMD_REQUEST_RESYNC = 43;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_REQUEST_BULLET = -11;
    /**
     * Client möchte RCON aufbauen.
     */
    public static final byte NET_TCP_CMD_REQUEST_RCON = 45;
    /**
     * Server antwortet auf die Client-Rconanfrage
     */
    public static final byte NET_TCP_CMD_ANSWER_RCON = 46;
    /**
     * Die maximale Ping, für die das Spiel noch flüssig läuft. Eine höhere Einstellung führt dazu, der Spieler alles stärker verzögert sieht, und die
     * eigene Spielfigur sich auch bei den anderen verzögert bewegt. Eine kleinere Zeit minimiert dieses Problem, wenn aber Pakete länger laufen,
     * kommt es zu lags.
     */
    public static final int NET_TICKSYNC_MAXPING = 100;
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
     * Die Größe der Chars für Kollision
     */
    public static final double CHARSIZE = 0.8;
    /**
     * Die Geschwindigkeit der Chars
     */
    public static final double CHARSPEED = 0.16;
    /**
     * Die HP der Chars
     */
    public static final int CHARHEALTH = 10;
    /**
     * Client-NetGraph per default an?
     */
    public static final boolean CLIENT_NETGRAPH_ON = true;
}
