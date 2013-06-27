package de._13ducks.spacebatz.client.graphics;

import java.io.File;
import org.newdawn.slick.opengl.Texture;

/**
 * Render-Tools.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class RenderUtils {

    /**
     * Lädt Texturen und erzeugt Bilder.
     */
    private static ImageLoader imageLoader = new ImageLoader(new File("").getAbsolutePath() + "/tex");
    /**
     * Die Größe und Breite der geladenen Textur.
     */
    private static int textureSize;

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
