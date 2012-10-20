package de._13ducks.spacebatz.client.graphics;

import java.io.File;
import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Lädt und verwaltet Bilder und Texturen.
 *
 * @author michael
 */
public class ImageLoader {

    /**
     * Die IDs der Texturen.
     */
    private HashMap<String, Texture> textures;

    /**
     * Lädt alle Texturen aus dem angegebenen Ordner.
     *
     * @param path
     */
    public ImageLoader(String path) {
        textures = new HashMap<>();
        try {
            File textureFolder = new File(path);
            File[] textureFiles = textureFolder.listFiles();
            for (int i = 0; i < textureFiles.length; i++) {
                File textureFile = textureFiles[i];
                if (textureFile.getName().contains(".png")) {
                    Texture tex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(textureFile.getPath()), GL_NEAREST);
                    textures.put(textureFile.getName(), tex);
                } else {
                    System.out.println("Textureloader: Ignoring " + textureFile.getName() + ": no png file.");
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Fehler beim Texturladen! ");

        }
    }

    public Texture getTexture(String name) {
        if (textures.containsKey(name)) {
            return textures.get(name);
        } else {
            throw new IllegalArgumentException("Textur " + name + " nicht gefunden!");
        }
    }
}
