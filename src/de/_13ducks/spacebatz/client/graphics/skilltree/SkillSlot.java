package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.client.graphics.ControlElement;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.shared.DefaultSettings;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

/**
 * Zeigt den Skill an der auf eine bestimmte Taste gelegt wurde.
 * Kann Skills per Drag and Drop aufnehmen, um sie auf diese Taste zu legen.
 *
 * @author michael
 */
public class SkillSlot extends ControlElement {

    /**
     * Die Taste die den Skill anspricht.
     */
    private byte targetKey;
    private int tile;
    private SkillTreeOverlay skilltree;
    private Texture texture;

    /**
     * Erzeugt einen neuen Skillbutton.
     *
     * @param tile die Tile auf der skilltree.png, die dieser Button rendern soll.
     * @param renderer
     */
    public SkillSlot(int tile, SkillTreeOverlay skilltree, byte key) {
        super();
        this.tile = tile;
        targetKey = key;
        this.skilltree = skilltree;
        texture = RenderUtils.getTextureByName("skilltree.png");
    }

    /**
     * Setzt die Tile die gerendert wird.
     */
    public void setTile(int tile) {
        this.tile = tile;
    }

    @Override
    public void render() {
        RenderUtils.setTilemap(texture);
        RenderUtils.setScreenMapping(0.0f, 1.0f, 0.0f, 1.0f);
        RenderUtils.setTileSize(32, 32);
        RenderUtils.drawTile(tile, getX(), getY(), getWidth(), getHeight());
        RenderUtils.restoreScreenMapping();
    }

    @Override
    public void onMouseButtonPressed() {
    }

    @Override
    public void onMouseButtonReleased() {
        if (isMouseOver((float) Mouse.getX() / DefaultSettings.CLIENT_GFX_RES_X, (float) Mouse.getY() / DefaultSettings.CLIENT_GFX_RES_Y)) {
            skilltree.stopDrag(targetKey);

        }
    }

    @Override
    public void onClick() {
    }
}
