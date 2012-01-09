package de._13ducks.spacebatz;

/**
 * Spieleinstellungen, der Einfachheit halber hier statisch erreichbar.
 * Achtung: Für Server und Client gleich!!
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
     * Frames auf dieses Anzahl limitieren.
     * Dingend empfohlen.
     * Hinweis: Hier wirkt auch vsync rein.
     * Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static final int CLIENT_GFX_FRAMELIMIT = 60;
    /**
     * VSync-Option.
     * Empfohlen, macht Bildschirmausgabe viel flüssiger.
     * Beeinflusst die Framerate direkt (double-buffering).
     * Auf den meisten Systemen wird die Framerate durch diese Option 60.
     * Hinweis: Die Framerate wird auch von FRAMELIMIT beeinfluss.
     * Die tatsächliche max-Framerate ist immer min(framelimit, vsync)
     */
    public static final boolean CLIENT_GFX_VSYNC = true;
    /**
     * Der Port auf dem der Server auf TCP-Verbindugen wartet.
     */
    public static final int SERVER_TCPPORT = 10000;
}
