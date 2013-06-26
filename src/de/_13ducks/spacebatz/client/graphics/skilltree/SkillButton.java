package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.client.graphics.util.Button;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import org.lwjgl.util.Color;

/**
 * Ein Button der einen Skill anzeigt. Kann angelkcickt werden, um den Skill zu verbessern. Kann per Drag and Drop gezogen werden.
 *
 * @author michael
 */
public class SkillButton extends Button {

    private byte level;
    private String skillName;
    private int tile;
    private SkillTreeOverlay skilltree;
    DynamicTileVAO buttonImage;
    private Color filter;
    private static Color red = new Color((byte) 100, (byte) 0, (byte) 0);
    private static Color white = new Color((byte) 255, (byte) 255, (byte) 255);

    /**
     * Erzeugt einen neuen Skillbutton.
     *
     * @param tile die Tile auf der skilltree.png, die dieser Button rendern soll.
     * @param renderer
     */
    public SkillButton(String name, int tile, SkillTreeOverlay skilltree, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.skillName = name;
        this.level = 0;
        this.tile = tile;
        this.skilltree = skilltree;
        buttonImage = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "skilltree.png", tile, 32);
    }

    @Override
    public void renderElement() {
        buttonImage.render();
//        TextWriter.renderText(String.valueOf(level), getX() + 0.05f, getY());
    }

    @Override
    public void onMouseButtonPressed(float x, float y, int button) {
        if (isMouseOver(x, y)) {
            skilltree.startDrag(skillName, tile);
        }
    }

    @Override
    public void onMouseButtonReleased(float x, float y, int button) {
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

    @Override
    public void keyboardInput(int key, boolean down) {
    }
}
