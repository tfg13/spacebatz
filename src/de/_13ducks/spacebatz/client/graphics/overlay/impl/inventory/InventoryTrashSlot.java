/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client.graphics.overlay.impl.inventory;

import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;

/**
 *
 * @author mekhar
 */
public class InventoryTrashSlot extends InventoryItemSlot {

    private InventoryOverlay inventory;
    DynamicTileVAO image;

    public InventoryTrashSlot(int x, int y, int width, int height, InventoryOverlay inventory, int slotID) {
        super(x, y, width, height, inventory, slotID);
        this.inventory = inventory;
        image = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "hud1.png", 0, 32);
    }

    @Override
    public void renderElement() {
        image.render();
    }

    @Override
    public void onMouseButtonReleased(float x, float y, int button) {
        if (isMouseOver(x, y)) {
            inventory.endDragInTrash();
        }
    }

    @Override
    public void onMouseButtonPressed(float x, float y, int button) {
    }

    @Override
    public void onClick() {
    }

    @Override
    public void keyboardInput(int key, boolean down) {
    }
}
