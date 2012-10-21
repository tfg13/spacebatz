package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.GameClient;
import java.io.File;

import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.TextWriter;
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
    private int imageWidth;
    private int imageHeight;
    private int imagesPerRow;

    /**
     * Erzeugt einen neuen Renderer.
     *
     * @param camera
     */
    Renderer(Camera camera) {
        this.camera = camera;
        textWriter = new TextWriter();
        imageLoader = new ImageLoader(new File("").getAbsolutePath() + "/tex");
        setImageSize(40, 40);
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
    }

    public void setImageSize(int width, int height) {
        imageHeight = height;
        imageWidth = width;
        imagesPerRow = (512 / width);
    }

    /**
     * Zeichnet ein Bild der aktuellen Textur auf den Bildschirm.
     *
     * @param index das wievielte Bild der textur (in Leserichtung) gezeichnet wird.
     * @param x X-Koordinate des Bildschirms an die gezeichnet wird
     * @param y Y-Koordinate des Bildschirms an die gezeichnet wird
     * @param width Breite mit der das Bild gezeichent wird
     * @param height Höhe mit der das Bild gezeichent wird
     */
    public void drawImage(int index, float x, float y, float width, float height) {
        glBegin(GL_QUADS); // Viereck zeichnen
        glEnable(GL_TEXTURE_2D);
        int row = index / imagesPerRow;
        int column = index % imagesPerRow;

        float fromX;
        float fromY;

        float toX;
        float toY;

        float textureX = column * imageWidth;
        float textureY = row * imageHeight;

        // links oben
        fromX = (float) textureX / 512;
        fromY = (float) textureY / 512;
        toX = x+width;
        toY = y+height;
        glTexCoord2f(fromX, fromY);
        glVertex2f(toX, toY);

        // rechts oben
        fromX = (float) (textureX + imageWidth) / 512;
        fromY = (float) textureY / 512;
        toX = x ;
        toY = y + height;
        glTexCoord2f(fromX, fromY);
        glVertex2f(toX, toY);

        // rechts unten
        fromX = (float) (textureX + imageWidth) / 512;
        fromY = (float) (textureY + imageHeight) / 512;
        toX = x ;
        toY = y ;
        glTexCoord2f(fromX, fromY);
        glVertex2f(toX, toY);

        // links unten
        fromX = (float) textureX / 512;
        fromY = (float) (textureY + imageHeight) / 512;
        toX = x + width;
        toY = y ;
        glTexCoord2f(fromX, fromY);
        glVertex2f(toX, toY);

        glEnd(); // Zeichnen des QUADs fertig
    }

    public void drawRectangle(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glDisable(GL_TEXTURE_2D);
        glColor3f(0, 0, 0.1f);
        glVertex2f(x, y);
        glVertex2f(x, y + height);
        glVertex2f(x + width, y + height);
        glVertex2f(x + width, y);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        glEnd();
    }
}
