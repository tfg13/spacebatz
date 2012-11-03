package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.graphics.ControlElement;
import de._13ducks.spacebatz.client.graphics.Renderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

/**
 * Ein Button der einen Skill anzeigt.
 * Kann angelkcickt werden, um den Skill zu verbessern.
 * Kann per Drag and Drop gezogen werden.
 *
 * @author michael
 */
public class SkillButton extends ControlElement {

    private byte level;
    private String skillName;
    private int tile;
    private SkillTreeControl skilltree;
    private Texture texture;
    private Color filter;
    private static Color red = new Color((byte) 100, (byte) 0, (byte) 0);
    private static Color white = new Color((byte) 255, (byte) 255, (byte) 255);

    /**
     * Erzeugt einen neuen Skillbutton.
     *
     * @param tile die Tile auf der skilltree.png, die dieser Button rendern soll.
     * @param renderer
     */
    public SkillButton(String name, int tile, SkillTreeControl skilltree, Renderer renderer) {
        super();
        this.skillName = name;
        this.level = 0;
        this.tile = tile;
        this.skilltree = skilltree;
        texture = renderer.getTextureByName("skilltree.png");
    }

    @Override
    public void render(Renderer renderer) {
        renderer.setTilemap(texture);
        renderer.setScreenMapping(0.0f, 1.0f, 0.0f, 1.0f);
        renderer.setTileSize(32, 32);
        renderer.drawTileColored(tile, getX(), getY(), getWidth(), getHeight(), filter);
        renderer.restoreScreenMapping();
        renderer.renderText(String.valueOf(level), getX() + 0.05f, getY());

    }

    @Override
    public void onMouseButtonPressed() {
        if (isMouseOver((float) Mouse.getX() / Settings.CLIENT_GFX_RES_X, (float) Mouse.getY() / Settings.CLIENT_GFX_RES_Y)) {
            skilltree.startDrag(skillName, tile);
        }
    }

    @Override
    public void onMouseButtonReleased() {
    }

    @Override
    public void onClick() {
        skilltree.investSkillPoint(skillName);
    }

    void setLevel(byte level) {
        this.level = level;

    }

    int getTile() {
        return tile;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(boolean available) {
        if (available) {
            filter = white;
        } else {
            filter = red;
        }
    }
}