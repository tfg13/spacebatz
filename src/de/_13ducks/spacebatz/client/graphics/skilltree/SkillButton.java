package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.util.Button;
import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

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
    private Texture texture;
    VAO buttonImage;
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
        texture = RenderUtils.getTextureByName("skilltree.png");
        buttonImage = VAOFactory.createDynamicTexturedRectVAO();
        float fromX = (float) 32 * ((float) tile % ((float) texture.getImageWidth() / (float) 32)) / (float) texture.getImageWidth();
        float fromY = 32 * (tile / (texture.getImageHeight() / 32)) / texture.getImageHeight();
        float textureWidth = (float) 32 / (float) texture.getImageWidth();
        float textureHeight = (float) 32 / (float) texture.getImageHeight();
        buttonImage.pushRectT(x, y, width, height, fromX, fromY, textureWidth, textureHeight);
        buttonImage.upload();
    }

    @Override
    public void renderElement() {
        RenderUtils.setTilemap(texture);
        buttonImage.render();
//        TextWriter.renderText(String.valueOf(level), getX() + 0.05f, getY());
    }

    @Override
    public void onMouseButtonPressed() {
        if (isMouseOver((float) Mouse.getX() / DefaultSettings.CLIENT_GFX_RES_X, (float) Mouse.getY() / DefaultSettings.CLIENT_GFX_RES_Y)) {
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

    @Override
    public void keyboardInput(int key, boolean down) {
    }
}
