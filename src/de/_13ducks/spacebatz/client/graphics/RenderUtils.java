package de._13ducks.spacebatz.client.graphics;

import java.io.File;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

/**
 * Rendert einfache Geometrie, Text und Bilder.
 *
 * @author michael
 */
public class RenderUtils {

    /**
     * Lädt Texturen und erzeugt Bilder.
     */
    private static ImageLoader imageLoader = new ImageLoader(new File("").getAbsolutePath() + "/tex");
    /**
     * Die Breite der Bilder in Pixel.
     */
    private static int imageWidth;
    /**
     * Die Höhe der Bilder in Pixel.
     */
    private static int imageHeight;
    /**
     * Die Zahl der Bilder pro Reihe auf der Textur.
     */
    private static int imagesPerRow;
    /**
     * Die Größe und Breite der geladenen Textur.
     */
    private static int textureSize;
    private static Color neutral = new Color(Color.WHITE);

    /**
     * Gibt die Textur mit dem angegebenen Namen zurück.
     *
     * @param name der Name der gesuchten Textur.
     * @return
     */
    public static Texture getTextureByName(String name) {
        return imageLoader.getTexture(name);
    }

    /**
     * Stellt ein welche Fläche auf dem Bildschirm abgebildet wird. Sichert die vorherige Abbildungsfläche, so dass sie mit restoreScreenMapping wieder hergestellt werden kann.
     *
     * Nach dem Aufruf von setScreenMapping(0,100,0,100) gilt folgendes: (0,0) ist die linke untere Ecke (50,50) ist die Mitte (100,100) ist die rechte obere Ecke
     *
     * @param x X-Startkoordinate
     * @param x2 X-Endkoordinate
     * @param y Y-Startkoordinate
     * @param y2 Y-Endkoordinate
     */
    public static void setScreenMapping(float x, float x2, float y, float y2) {
        glPushMatrix();
        glLoadIdentity();
        glOrtho(x, x2, y, y2, -1, 1);
    }

    /**
     * Macht den letzten Aufruf von setScreenMapping rückgängig.
     */
    public static void restoreScreenMapping() {
        glPopMatrix();
    }

    /**
     * Setzt die Textur von der gerade gezeichnet wird.
     *
     * @param texture
     */
    public static void setTilemap(Texture texture) {
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
    public static void setTileSize(int width, int height) {
        imageHeight = height;
        imageWidth = width;
        imagesPerRow = (textureSize / width);
    }

    /**
     * Zeichnet das angegebene Bild.
     *
     * @param image
     */
    public static void drawImage(Image image) {
        setTilemap(image.getTilemap());
        drawTileColored(image.getIndex(), image.getPosX(), image.getPosY(), image.getWidth(), image.getHeight(), neutral);
    }

    /**
     * Zeichnet das X-te Bild (in Leserichtung) der aktuellen Textur eingefärbt auf den Bildschirm. Die Größe der Bilder wird mit setTileSize() eingestellt. Die Koordinaten werden
     * auf den Bereich abgebilder, der mit setScreenMapping() eingestellt wurde.
     *
     * @param index das wievielte Bild der Textur (in Leserichtung) gezeichnet wird.
     * @param x X-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param y Y-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param width Breite mit der das Bild gezeichent wird (in Prozent)
     * @param height Höhe mit der das Bild gezeichent wird (in Prozent)
     * @param color die Farbe
     */
    public static void drawTileColored(int index, float x, float y, float width, float height, Color color) {

        glBegin(GL_QUADS); // Viereck zeichnen
        glEnable(GL_TEXTURE_2D); // Textur zeichnen

        glColor3ub(color.getRedByte(), color.getGreenByte(), color.getBlueByte()); // Farbe setzen

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
        fromX = textureX / textureSize;
        fromY = textureY / textureSize;
        toX = x;
        toY = y + height;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // rechts unten
        fromX = (textureX + imageWidth) / textureSize;
        fromY = textureY / textureSize;
        toX = x + width;
        toY = y + height;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // rechts oben
        fromX = (textureX + imageWidth) / textureSize;
        fromY = (textureY + imageHeight) / textureSize;
        toX = x + width;
        toY = y;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        // links oben
        fromX = textureX / textureSize;
        fromY = (textureY + imageHeight) / textureSize;
        toX = x;
        toY = y;
        glTexCoord2f(fromX, fromY);
        glVertex2d(toX, toY);

        glEnd(); // Zeichnen des QUADs fertig
    }

    /**
     * Zeichnet das X-te Bild (in Leserichtung) der aktuellen Textur auf den Bildschirm. Die Größe der Bilder wird mit setTileSize() eingestellt. Die Koordinaten werden auf den
     * Bereich abgebilder, der mit setScreenMapping() eingestellt wurde.
     *
     * @param index das wievielte Bild der Textur (in Leserichtung) gezeichnet wird.
     * @param x X-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param y Y-Koordinate des Bildschirms an die gezeichnet wird (in Prozent)
     * @param width Breite mit der das Bild gezeichent wird (in Prozent)
     * @param height Höhe mit der das Bild gezeichent wird (in Prozent)
     */
    public static void drawTile(int index, float x, float y, float width, float height) {
        drawTileColored(index, x, y, width, height, neutral);
    }

    /**
     * Zeichnet ein gefärbtes ausgefülltes Rechteck auf den Bildschirm. Die Farbe kann mit setColor gesetzt werden.
     *
     * @param x die X-Koordinate der linken oberen Ecke
     * @param y die Y-Koordinate der linken oberen Ecke
     * @param width die Breite des Rechtecks
     * @param height die Höhe des Rechtecks
     * @param color die Farbe des Rechtecks
     */
    public static void drawRectangle(float x, float y, float width, float height, Color color) {

        glPushMatrix(); // Transformationsmatrix sichern

        // Bildschirm auf 0.0-1.0 / 0.0-1.0 abbilden:
        glLoadIdentity();
        glOrtho(0, 1, 0, 1, -1, 1);
        glDisable(GL_TEXTURE_2D); // keine Textur sondern Geometrie zeichnen
        glBegin(GL_QUADS); // Viereck zeichnen
        glColor3ub(color.getRedByte(), color.getGreenByte(), color.getBlueByte());
        glVertex2d(x, y);
        glVertex2d(x + width, y);
        glVertex2d(x + width, y + width);
        glVertex2d(x, y + width);

        glEnd(); // Zeichnen des QUADs fertig
        glPopMatrix();// wieder die ursprüngliche Transformationsmatrix herstellen
    }

    public static float getSourceXForTile(Texture texture, int tile, int tileSize) {
        return (float) tileSize * ((float) tile % ((float) texture.getImageWidth() / (float) tileSize)) / (float) texture.getImageWidth();
    }

    public static float getSourceYForTile(Texture texture, int tile, int tileSize) {
        return tileSize * (tile / (texture.getImageHeight() / tileSize)) / texture.getImageHeight();
    }

    public static float getSourceWidthForTile(Texture texture, int tile, int tileSize) {
        return (float) tileSize / (float) texture.getImageWidth();
    }

    public static float getSourceHeightForTile(Texture texture, int tile, int tileSize) {
        return (float) tileSize / (float) texture.getImageHeight();
    }

    private RenderUtils() {
    }
}
