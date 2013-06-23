package de._13ducks.spacebatz.client.graphics;

import java.io.File;
import org.newdawn.slick.opengl.Texture;

/**
 * Rendert-Tools.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class RenderUtils {

    /**
     * Lädt Texturen und erzeugt Bilder.
     */
    private static ImageLoader imageLoader = new ImageLoader(new File("").getAbsolutePath() + "/tex");

    /**
     * Gibt die Textur mit dem angegebenen Namen zurück.
     *
     * @param name der Name der gesuchten Textur.
     * @return
     */
    public static Texture getTextureByName(String name) {
        return imageLoader.getTexture(name);
    }

    private RenderUtils() {
    }
}
