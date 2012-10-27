package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.graphics.ControlElement;
import de._13ducks.spacebatz.client.graphics.Renderer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author michael
 */
public class SkillSlot extends ControlElement {

    private byte targetKey;
    private int tile;
    private SkillTreeControl skilltree;
    private Texture texture;

    /**
     * Erzeugt einen neuen Skillbutton.
     *
     * @param tile die Tile auf der skilltree.png, die dieser Button rendern soll.
     * @param renderer
     */
    public SkillSlot(int tile, SkillTreeControl skilltree, byte key, Renderer renderer) {
        super();
        this.tile = tile;
        targetKey = key;
        this.skilltree = skilltree;
        texture = renderer.getTextureByName("skilltree.png");
    }

    /**
     * Setzt die Tile die gerendert wird.
     */
    public void setTile(int tile) {
        this.tile = tile;
    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTilemap(texture);
        renderer.setScreenMapping(0.0f, 1.0f, 0.0f, 1.0f);
        renderer.setTileSize(32, 32);
        renderer.drawTile(tile, getX(), getY(), getWidth(), getHeight());
        renderer.restoreScreenMapping();
        renderer.resetColor();
    }

    @Override
    public void onMouseButtonPressed() {
    }

    @Override
    public void onMouseButtonReleased() {
        if (isMouseOver((float) Mouse.getX() / Settings.CLIENT_GFX_RES_X, (float) Mouse.getY() / Settings.CLIENT_GFX_RES_Y)) {
            skilltree.stopDrag(targetKey);

        }
    }

    @Override
    public void onClick() {
    }
}
