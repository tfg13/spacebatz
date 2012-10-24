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

    public Texture getTextureByName(String name) {
        return imageLoader.getTexture(name);

    }

    /**
     * Setzt die Textur von der gerade gezeichnet wird.
     *
     * @param texture
     */
    public void setTexture(Texture texture) {
        texture.bind();
        textureSize = texture.getImageWidth();
        if (texture.getImageHeight() != texture.getImageWidth()) {
            throw new IllegalArgumentException("Texturen müssen quadratisch sein!");
        }
    }

    public void setImageSize(int width, int height) {
        imageHeight = height;
        imageWidth = width;
        imagesPerRow = (textureSize / width);
    }

    /**
     * Zeichnet das X-te Bild (in Leserichtung) der aktuellen Textur auf den Bildschirm.
     * Die Größe der Bilder wird mit setImageSize() eingestellt.
     *
     * Koordinaten werden in Prozent des Bildschirms angegeben:
     * 0.0/0.0 links unten
     * 0.5/0.5 Mitte
     * 1.0/1.0 rechts oben
     *
     * @param index das wievielte Bild der Textur (in Leserichtung) gezeichnet wird.
     * @param x X-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param y Y-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param width Breite mit der das Bild gezeichent wird (in Prozent)
     * @param height Höhe mit der das Bild gezeichent wird (in Prozent)
     */
    public void drawImage(int index, float x, float y, float width, float height) {

        glPushMatrix(); // Transformationsmatrix sichern
        // Neue Matrix erstellen: die bildet den Bildschirm auf ein 100*100 Feld ab
        // 0/0 ist dann links unten, 50/50 ist dann die Mitte und 100/100 rechts oben.
        glLoadIdentity();
        glOrtho(0, 1, 0, 1, -1, 1);

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
        glPopMatrix();// wieder die ursprüngliche Transformationsmatrix herstellen
    }

    /**
     * Setzt die Farbe die zum Zeichnen verwendet wird.
     *
     * @param r
     * @param g
     * @param b
     */
    public void setColor(float r, float g, float b) {
        glColor3f(r, g, b);
    }

    /**
     * Setzt die Farbe wieder auf den Standard.
     */
    public void resetColor() {
        setColor(1.0f, 1.0f, 1.0f);
    }

    /**
     * Rendert Text auf den Bildschirm.
     *
     * @param text der Text der gerendert wird.
     * @param x X-Koordinate in % des Bildschirms (0.5f ist die Mitte).
     * @param y Y-Koordinate in % des Bildschirms (0.5f ist die Mitte).
     */
    public void renderText(String text, float x, float y) {
        textWriter.renderText(text, x * camera.getTilesX(), y * camera.getTilesY());
    }

    public void drawRectangle(float x, float y, float width, float height, float r, float g, float b) {

        glPushMatrix(); // Transformationsmatrix sichern

        // Bildschirm auf 0.0-1.0 / 0.0-1.0 abbilden:
        glLoadIdentity();
        glOrtho(0, 1, 0, 1, -1, 1);

        glBegin(GL_QUADS); // Viereck zeichnen
        glDisable(GL_TEXTURE_2D); // Textur zeichnen

        glColor3f(r, g, b);

        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y + width);
        glVertex2d(x, y + width);

        glEnd(); // Zeichnen des QUADs fertig
        glPopMatrix();// wieder die ursprüngliche Transformationsmatrix herstellen
    }
}
