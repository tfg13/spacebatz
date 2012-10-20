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
    public void drawImage(int index, float a, float b, float width, float height) {

        getTextureByName("ground.png").bind();
        glBegin(GL_QUADS); // Viereck zeichnen

        for (int x = -(int) (1 + getCamera().getPanX()); x < -(1 + getCamera().getPanX()) + camera.getTilesX() + 2; x++) {
            for (int y = -(int) (1 + getCamera().getPanY()); y < -(1 + getCamera().getPanY()) + camera.getTilesY() + 2; y++) {
                int tex = 1;
                int tx = tex % 16;
                int ty = tex / 16;
                glTexCoord2f(tx * 0.0625f, ty * 0.0625f); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
                glVertex3f(x + getCamera().getPanX(), y + 1 + getCamera().getPanY(), 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
                // Die weiteren 3 Ecken im Uhrzeigersinn:
                glTexCoord2f((tx + 1) * 0.0625f, ty * 0.0625f);
                glVertex3f(x + 1 + getCamera().getPanX(), y + 1 + getCamera().getPanY(), 0);
                glTexCoord2f((tx + 1) * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + 1 + getCamera().getPanX(), y + getCamera().getPanY(), 0);
                glTexCoord2f(tx * 0.0625f, (ty + 1) * 0.0625f);
                glVertex3f(x + getCamera().getPanX(), y + getCamera().getPanY(), 0);
            }
        }
        glEnd();

      
//        int row = index / imagesPerRow;
//        int column = index % imagesPerRow;
//
//        float fromX;
//        float fromY;
//
//        float toX;
//        float toY;
//
//        // links oben
//        fromX = column * imageWidth;
//        fromY = row * imageHeight;
//        toX = getCamera().getPanX() + x;
//        toY = getCamera().getPanY() + y;
//        glTexCoord2f(fromX, fromY);
//        glVertex2f(toX, toY);
//
//        // rechts oben
//        fromX = column * imageWidth + imageWidth;
//        fromY = row * imageHeight;
//        toX = getCamera().getPanX() + x + width;
//        toY = getCamera().getPanY() + y;
//        glTexCoord2f(fromX, fromY);
//        glVertex2f(toX, toY);
//
//        // rechts unten
//        fromX = column * imageWidth + imageWidth;
//        fromY = row * imageHeight + imageHeight;
//        toX = getCamera().getPanX() + x + width;
//        toY = getCamera().getPanY() + y + height;
//        glTexCoord2f(fromX, fromY);
//        glVertex2f(toX, toY);
//
//        // links unten
//        fromX = column * imageWidth;
//        fromY = row * imageHeight + imageHeight;
//        toX = getCamera().getPanX() + x;
//        toY = getCamera().getPanY() + y + height;
//        glTexCoord2f(fromX, fromY);
//        glVertex2f(toX, toY);

        glEnd(); // Zeichnen des QUADs fertig
    }
}
