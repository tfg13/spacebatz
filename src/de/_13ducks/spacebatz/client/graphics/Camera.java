package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.shared.DefaultSettings;
import static de._13ducks.spacebatz.shared.DefaultSettings.*;
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
    private float tilesX, tilesY;
    /**
     * Der aktuelle Zoomfaktor. Wird benötigt, um Schriften immer gleich groß anzeigen zu können
     */
    private float zoomFactor;
    /**
     * Scrollen des Bildschirms, in Feldern.
     */
    private float panX, panY;

    /**
     * Erzeugt die Kamera und stellt den Standardzoom ein.
     * @param resX Pixel in X-Richtung
     * @param resY Pixel in Y-Richtung
     */
    public Camera(int resX, int resY) {
        if (!DefaultSettings.CLIENT_GFX_LOOKAHEAD) {
            // Zoom korrekt berechnen. Man sieht immer 58 * 34 Felder weit.
            // Höhe hat Prio, bei 4:3 sieht man weniger...
            setZoomFact(resY / 34.0f / CLIENT_GFX_TILESIZE);
        } else {
            // Bei Lookahead sieht man weniger weit, weil man ja die Ansicht verschieben kann.
            setZoomFact(resY / 20.f / CLIENT_GFX_TILESIZE);
        }
    }

    /**
     * Setzt den ZoomFaktor der Kamera.
     *
     * @param zoomFact
     */
    public final void setZoomFact(float zoomFact) {
        glLoadIdentity();
        GLU.gluOrtho2D(0f, CLIENT_GFX_RES_X / (CLIENT_GFX_TILESIZE * zoomFact), 0f, CLIENT_GFX_RES_Y / (CLIENT_GFX_TILESIZE * zoomFact));
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

    /**
     * @return the panX
     */
    public float getPanX() {
        return panX;
    }

    /**
     * @return the panY
     */
    public float getPanY() {
        return panY;
    }

    /**
     * @param panX the panX to set
     */
    public void setPanX(float panX) {
        this.panX = panX;
    }

    /**
     * @param panY the panY to set
     */
    public void setPanY(float panY) {
        this.panY = panY;
    }
}
