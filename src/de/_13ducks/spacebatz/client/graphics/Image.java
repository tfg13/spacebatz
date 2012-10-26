package de._13ducks.spacebatz.client.graphics;

import org.newdawn.slick.opengl.Texture;

/**
 * Ein Bild auf einer Tilemap.
 *
 * @author michael
 */
public class Image {

    private float posX;
    private float posY;
    private float width;
    private float height;
    private Texture tilemap;
    private int index;

    public Image(float x, float y, float width, float height, Texture texture, int index) {
        posX = x;
        posY = y;
        this.width = width;
        this.height = height;
        this.tilemap = texture;
        this.index = index;
    }

    public Image() {
    }

    /**
     * @return the posX
     */
    public float getPosX() {
        return posX;
    }

    /**
     * @param posX the posX to set
     */
    public void setPosX(float posX) {
        this.posX = posX;
    }

    /**
     * @return the posY
     */
    public float getPosY() {
        return posY;
    }

    /**
     * @param posY the posY to set
     */
    public void setPosY(float posY) {
        this.posY = posY;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * @return the tilemap
     */
    public Texture getTilemap() {
        return tilemap;
    }

    /**
     * @param tilemap the tilemap to set
     */
    public void setTilemap(Texture tilemap) {
        this.tilemap = tilemap;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
