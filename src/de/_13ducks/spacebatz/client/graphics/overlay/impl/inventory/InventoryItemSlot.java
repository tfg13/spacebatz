package de._13ducks.spacebatz.client.graphics.overlay.impl.inventory;

import de._13ducks.spacebatz.client.graphics.util.Button;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTexturedRectangleVAO;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;

/**
 *
 * @author mekhar
 */
public class InventoryItemSlot extends Button {

    protected InventoryOverlay inventory;
    private int slotID;
    private DynamicTileVAO image;
    private DynamicTexturedRectangleVAO background;
    private int tile = -1;

    public InventoryItemSlot(int x, int y, int width, int height, InventoryOverlay inventory, int slotID) {
        super(x, y, width, height);
        this.inventory = inventory;
        image = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "item.png", 0, 32);
        background = VAOFactory.createDynamicTexturedRectangleVAO(x, y, width, height, "hud1.png", 273, 206, 180, 101);
    }

    @Override
    public void onMouseButtonPressed(float x, float y, int button) {
        if (isMouseOver(x, y) && (tile != -1)) {
            inventory.startDrag(slotID, tile);
        }
    }

    @Override
    public void onMouseButtonReleased(float x, float y, int button) {
        if (isMouseOver(x, y)) {
            inventory.endDrag(slotID);
        }
    }

    @Override
    public void onClick() {
    }

    @Override
    public void keyboardInput(int key, boolean down) {
    }

    public void setTile(int tile) {
        this.tile = tile;
        if (tile != -1) {
            image.setSourceTile(tile);
        }
    }

    @Override
    public void renderElement() {
        background.render();
        if (tile != -1) {
            image.render();
        }

    }

    public int getTile() {
        return tile;
    }
}
