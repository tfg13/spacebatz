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
     * Die zuletzt gebindete Textur.
     */
    private static Texture bindTex;

    /**
     * Bindet eine Textur.
     * Die Textur wird nur gebunden, wenn sie es nicht schon ist.
     *
     * @param name der Name der Texturdatei (ohne tex/ Pfad)
     */
    public static void bindTexture(String name) {
        Texture tex = imageLoader.getTexture(name);
        if (tex == null) {
            System.out.println("WARNING: GFX: Missing Texture \"" + name + "\"");
            return;
        }
        if (bindTex != tex) {
            tex.bind();
            bindTex = tex;
        }
    }

    /**
     * Gibt die Textur mit dem angegebenen Namen zurück.
     *
     * @param name der Name der gesuchten Textur.
     * @return
     */
    private static Texture getTextureByName(String name) {
        return imageLoader.getTexture(name);
    }

    public static float getSourceXForTile(String name, int tile, int tileSize) {
        Texture texture = getTextureByName(name);
        return (float) tileSize * ((float) tile % ((float) texture.getImageWidth() / (float) tileSize)) / (float) texture.getImageWidth() + (float) 1 / (float) texture.getImageWidth();
    }

    public static float getSourceYForTile(String name, int tile, int tileSize) {
        Texture texture = getTextureByName(name);
        return (float) tileSize * (tile / (texture.getImageHeight() / tileSize)) / (float) texture.getImageHeight() + (float) 1 / (float) texture.getImageHeight();
    }

    public static float getSourceWidthForTile(String name, int tile, int tileSize) {
        Texture texture = getTextureByName(name);
        return (float) (tileSize) / (float) texture.getImageWidth() - (float) 2 / (float) texture.getImageWidth();
    }

    public static float getSourceHeightForTile(String name, int tile, int tileSize) {
        Texture texture = getTextureByName(name);
        return (float) (tileSize) / (float) texture.getImageHeight() - (float) 2 / (float) texture.getImageHeight();
    }

    public static float getTextureWidth(String name) {
        return getTextureByName(name).getImageWidth();
    }

    public static float getTextureHeight(String name) {
        return getTextureByName(name).getImageHeight();
    }

    private RenderUtils() {
    }
}
