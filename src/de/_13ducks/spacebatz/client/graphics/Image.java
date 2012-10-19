package de._13ducks.spacebatz.client.graphics;

import org.newdawn.slick.opengl.Texture;

/**
 * Ein bild ist eine Fläche auf einer Textur.
 *
 * @author michael
 */
public class Image {

    private Texture sourceTexture;
    private int x1;
    private int x2;
    private int width;
    private int height;

    public Image(Texture texture, int x, int y, int width, int height) {
        this.x1 = x;
        this.width = width;
        this.x2 = y;
        this.height = height;
        this.sourceTexture = texture;
    }
}
