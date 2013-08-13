package de._13ducks.spacebatz.client.graphics.skilltree;

import de._13ducks.spacebatz.client.graphics.util.Button;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;

/**
 * Zeigt den Skill an der auf eine bestimmte Taste gelegt wurde. Kann Skills per Drag and Drop aufnehmen, um sie auf diese Taste zu legen.
 *
 * @author michael
 */
public class SkillSlot extends Button {

    /**
     * Die Taste die den Skill anspricht.
     */
    private byte targetKey;
    private SkillTreeOverlay skilltree;
    DynamicTileVAO image;

    /**
     * Erzeugt einen neuen Skillbutton.
     *
     * @param tile die Tile auf der skilltree.png, die dieser Button rendern soll.
     * @param renderer
     */
    public SkillSlot(int tile, SkillTreeOverlay skilltree, byte key, int x, int y, int width, int height) {
        super(x, y, width, height);
        targetKey = key;
        this.skilltree = skilltree;
        image = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "skilltree.png", tile, 32);
    }

    /**
     * Setzt die Tile die gerendert wird.
     */
    public void setTile(int tile) {
        image.setSourceTile(tile);
    }

    @Override
    public void renderElement() {
        image.render();
    }

    @Override
    public void onMouseButtonPressed(float x, float y, int button) {
    }

    @Override
    public void onMouseButtonReleased(float x, float y, int button) {
        if (isMouseOver(x, y)) {
            skilltree.stopDrag(targetKey);

        }
    }

    @Override
    public void onClick() {
    }

    @Override
    public void keyboardInput(int key, boolean down) {
    }
}
