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
     * Frames auf dieses Anzahl limitieren. Dingend empfohlen. Hinweis: Hier wirkt auch vsync rein. Die tatsächliche max-Framerate ist immer min(framelimit,
     * vsync)
     */
    public static final int CLIENT_GFX_FRAMELIMIT = 60;
    /**
     * VSync-Option. Empfohlen, macht Bildschirmausgabe viel flüssiger. Beeinflusst die Framerate direkt (double-buffering). Auf den meisten Systemen wird die
     * Framerate durch diese Option 60. Hinweis: Die Framerate wird auch von FRAMELIMIT beeinfluss. Die tatsächliche max-Framerate ist immer min(framelimit,
     * vsync)
     */
    public static final boolean CLIENT_GFX_VSYNC = true;
    /**
     * Der Port auf dem der Server auf TCP-Verbindugen wartet.
     */
    public static final int SERVER_TCPPORT = 10000;
    /**
     * Auf diesem Port ist der Server per UDP zu erreichen.
     * Darf nicht der gleiche sein, wie CLIENT_UDPPORT
     */
    public static final int SERVER_UDPPORT = 13947;
    /**
     * Auf diesem Port ist der Client per UDP zu erreichen.
     * Darf nicht der gleiche sein, wie SERVER_UDPPORT
     */
    public static final int CLIENT_UDPPORT = 13948;
    /**
     * Die Server-Tickrate. Guter Wert muss empirisch ermittelt werden.
     */
    public static final int SERVER_TICKRATE = 60;
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
    public static final byte NET_UDP_CMD_NORMAL_CHAR_UPDATE = 10;
    /**
     * Bullet erzeugen.
     */
    public static final byte NET_UDP_CMD_SPAWN_BULLET = 11;
    /**
     * Bestätigung, dass eine Bewegung vom Server beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_MOVE = 12;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_INPUT = -10;
    /**
     * Die cmdID für Level-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_LEVEL = 20;
    /**
     * Die cmdID für EnemyTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_ENEMYTYPES= 29;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_REQUEST_BULLET = -11;
    /**
     * Die maximale Ping, für die das Spiel noch flüssig läuft.
     * Eine höhere Einstellung führt dazu, der Spieler alles stärker verzögert sieht,
     * und die eigene Spielfigur sich auch bei den anderen verzögert bewegt.
     * Eine kleinere Zeit minimiert dieses Problem, wenn aber Pakete länger laufen, kommt es zu lags.
     */
    public static final int NET_TICKSYNC_MAXPING = 100;
    /**
     * Maximale Anzahl Spieler gleichzeitig auf einem Server. Darf ohne Änderungen am Code 127 nicht übersteigen!
     */
    public static final int SERVER_MAXPLAYERS = 32;
    /**
     * Die Distanz, ab der arbeitslose Mobs Chars verfolgen
     */
    public static double SERVER_MOB_AGGRO_RANGE = 7.0;
    /**
     * Die Reichweite für Kollisionen
     */
    public static double SERVER_COLLISION_DISTANCE = 1.5;
}
