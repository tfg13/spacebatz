package de._13ducks.spacebatz.client.graphics;

import java.io.File;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 * Rendert einfache Geometrie, Text und Bilder.
 *
 * @author michael
 */
public class Renderer {

    /**
     * Der Text-Renderer.
     */
    private TextWriter textWriter;
    /**
     * Die Kamera der Engine.
     */
    private Camera camera;
    /**
     * Lädt Texturen und erzeugt Bilder.
     */
    private ImageLoader imageLoader;
    /**
     * Die Breite der Bilder in Pixel.
     */
    private int imageWidth;
    /**
     * Die Höhe der Bilder in Pixel.
     */
    private int imageHeight;
    /**
     * Die Zahl der Bilder pro Reihe auf der Textur.
     */
    private int imagesPerRow;
    /**
     * Die Größe und Breite der geladenen Textur.
     */
    private int textureSize;

    /**
     * Erzeugt einen neuen Renderer.
     *
     * @param camera
     */
    Renderer(Camera camera) {
        this.camera = camera;
        textWriter = new TextWriter();
        imageLoader = new ImageLoader(new File("").getAbsolutePath() + "/tex");
    }

    /**
     * @return the textWriter
     */
    public TextWriter getTextWriter() {
        return textWriter;
    }

    /**
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Gibt die Textur mit dem angegebenen Namen zurück.
     *
     * @param name der Name der gesuchten Textur.
     * @return
     */
    public Texture getTextureByName(String name) {
        return imageLoader.getTexture(name);
    }

    /**
     * Stellt ein welche Fläche auf dem Bildschirm abgebildet wird.
     * Sichert die vorherige Abbildungsfläche, so dass sie mit restoreScreenMapping wieder hergestellt werden kann.
     *
     * Nach dem Aufruf von setScreenMapping(0,100,0,100) gilt folgendes:
     * (0,0) ist die linke untere Ecke
     * (50,50) ist die Mitte
     * (100,100) ist die rechte obere Ecke
     *
     * @param x X-Startkoordinate
     * @param x2 X-Endkoordinate
     * @param y Y-Startkoordinate
     * @param y2 Y-Endkoordinate
     */
    public void setScreenMapping(float x, float x2, float y, float y2) {
        glPushMatrix();
        glLoadIdentity();
        glOrtho(x, x2, y, y2, -1, 1);
    }

    /**
     * Macht den letzten Aufruf von setScreenMapping rückgängig.
     */
    public void restoreScreenMapping() {
        glPopMatrix();
    }

    /**
     * Setzt die Textur von der gerade gezeichnet wird.
     *
     * @param texture
     */
    public void setTilemap(Texture texture) {
        texture.bind();
        textureSize = texture.getImageWidth();
        if (texture.getImageHeight() != texture.getImageWidth()) {
            throw new IllegalArgumentException("Texturen müssen quadratisch sein!");
        }
    }

    /**
     * Setzt die Größe der Tiles, die verwendet wird um (mit drawTile()) von Tilemaps zu zeichnen.
     *
     * @param width die Breite der Tiles in Pixel
     * @param height die Höhe der Tiles in Pixel
     */
    public void setTileSize(int width, int height) {
        imageHeight = height;
        imageWidth = width;
        imagesPerRow = (textureSize / width);
    }

    /**
     * Zeichnet das X-te Bild (in Leserichtung) der aktuellen Textur auf den Bildschirm.
     * Die Größe der Bilder wird mit setTileSize() eingestellt.
     * Die Koordinaten werden auf den Bereich abgebilder, der mit setScreenMapping() eingestellt wurde.
     *
     * @param index das wievielte Bild der Textur (in Leserichtung) gezeichnet wird.
     * @param x X-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param y Y-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param width Breite mit der das Bild gezeichent wird (in Prozent)
     * @param height Höhe mit der das Bild gezeichent wird (in Prozent)
     */
    public void drawTile(int index, float x, float y, float width, float height) {

        glBegin(GL_QUADS); // Viereck zeichnen
        glEnable(GL_TEXTURE_2D); // Textur zeichnen

        int row = index / imagesPerRow; // Die Reihe in der das gesuchte Bild ist
        int column = index % imagesPerRow; // Die Zeile in der das Bild ist

        float fromX; // X-Koordinate der Vertex auf der Textur (in Prozent)
        float fromY; // Y-Koordinate der Vertex auf der Textur (in Prozent)

        double toX; // X-Koordinate der Vertex auf dem Bildschirm (in Prozent)
        double toY; // Y-Koordinate der Vertex auf dem Bildschirm (in Prozent)

        float textureX = column * imageWidth; // X-Koordinate der Vertex auf der Textur in Pixel
        float textureY = row * imageHeight; // Y-Koordinate der Vertex auf der Textur in Pixel

        // alle Angaben über rechts, links, oben und unten sind ohne Gewähr
        // links unten
        fromX = (float) textureX / textureSize;
        fromY = (float) textureY / textureSize;
        toX = x;
        toY = y + height;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // rechts unten
        fromX = (float) (textureX + imageWidth) / textureSize;
        fromY = (float) textureY / textureSize;
        toX = x + width;
        toY = y + height;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // rechts oben
        fromX = (float) (textureX + imageWidth) / textureSize;
        fromY = (float) (textureY + imageHeight) / textureSize;
        toX = x + width;
        toY = y;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // links oben
        fromX = (float) textureX / textureSize;
        fromY = (float) (textureY + imageHeight) / textureSize;
        toX = x;
        toY = y;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        glEnd(); // Zeichnen des QUADs fertig
    }

    /**
     * Setzt den Farbfilter.
     *
     * @param r der Rotanteil
     * @param g der Grünanteil
     * @param b der Gelbanteil
     */
    public void setColor(float r, float g, float b) {
        glColor3f(r, g, b);
    }

    /**
     * Setzt den Farbfilter zurück.
     */
    public void resetColor() {
        setColor(1.0f, 1.0f, 1.0f);
    }

    /**
     * Rendert Text auf den Bildschirm.
     * ACHTUNG braucht die setScreenMapping()-Einstellung des GodControls!
     *
     * @param text der Text der gerendert wird.
     * @param x X-Koordinate in % des Bildschirms (0.5f ist die Mitte).
     * @param y Y-Koordinate in % des Bildschirms (0.5f ist die Mitte).
     */
    public void renderText(String text, float x, float y) {
        textWriter.renderText(text, x * camera.getTilesX(), y * camera.getTilesY());
    }

    /**
     * Zeichnet ein ausgefülltes Rechteck mit der aktuellen Farbe auf den Bildschirm.
     * Die Farbe kann mit setColor gesetzt werden.
     *
     * @param x die X-Koordinate der linken oberen Ecke
     * @param y die Y-Koordinate der linken oberen Ecke
     * @param width die Breite des Rechtecks
     * @param height die Höhe des Rechtecks
     */
    public void drawRectangle(float x, float y, float width, float height) {

        glPushMatrix(); // Transformationsmatrix sichern

        // Bildschirm auf 0.0-1.0 / 0.0-1.0 abbilden:
        glLoadIdentity();
        glOrtho(0, 1, 0, 1, -1, 1);

        glBegin(GL_QUADS); // Viereck zeichnen
        glDisable(GL_TEXTURE_2D); // keine Textur sondern Geometrie zeichnen

        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y + width);
        glVertex2d(x, y + width);

        glEnd(); // Zeichnen des QUADs fertig
        glPopMatrix();// wieder die ursprüngliche Transformationsmatrix herstellen
    }
}
