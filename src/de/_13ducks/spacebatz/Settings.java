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
     */
    public static final int SERVER_UDPPORT = 13947;
    /**
     * Auf diesem Port ist der Client per UDP zu erreichen.
     */
    public static final int CLIENT_UDPPORT = 13947;
    /**
     * Die Server-Tickrate. Guter Wert muss empirisch ermittelt werden.
     */
    public static final int SERVER_TICKRATE = 60;
    /**
     * Wieviel der Server maximal hinter seiner Target-Tickrate sein darf, bevor eine Warnung ausgespuckt wird.
     */
    public static final int SERVER_TICKMISS_MAX_NANOS = 125000000;
    /**
     * Größe des Empfangspuffers für ein Clientpacket auf Serverseite.
     * NICHT EINFACH VERÄNDERN!
     */
    public static final int NET_UDP_CTS_SIZE = 7;
    
    /**
     * Größe des Empfangspuffers für ein Serverpacket auf Clientseite.
     * NICHT EINFACH VERÄNDERN!
     */
    public static final int NET_UDP_STC_MAX_SIZE = 512;
    
    /**
     * Normales Einheitenupdate, das Regelmäßig verschickt wird.
     */
    public static final byte NET_UDP_CMD_NORMAL_CHAR_UPDATE = 10;
}
