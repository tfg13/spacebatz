package de._13ducks.spacebatz.client.graphics;

import static de._13ducks.spacebatz.Settings.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author michael
 */
public class Camera {

    /**
     * Die Anzahl der Tiles auf dem Bildschirm.
     */
    private int tilesX, tilesY;
    /**
     * Der aktuelle Zoomfaktor. Wird benötigt, um Schriften immer gleich groß anzeigen zu können
     */
    private int zoomFactor;

    /**
     * Erzeugt die Kamera und stellt den Standardzoom ein.
     */
    public Camera() {
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * CLIENT_GFX_TILEZOOM));
        zoomFactor = 2;
    }

    /**
     * Setzt den ZoomFaktor der KAmera.
     *
     * @param zoomFact
     */
    public void setZoomFact(int zoomFact) {
        glLoadIdentity();
        GLU.gluOrtho2D(0, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact), 0, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
        tilesX = (int) Math.ceil(CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact));
        tilesY = (int) Math.ceil(CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
        zoomFactor = zoomFact;
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public float getTilesX() {
        return tilesX;
    }

    public float getTilesY() {
        return tilesY;
    }
}
