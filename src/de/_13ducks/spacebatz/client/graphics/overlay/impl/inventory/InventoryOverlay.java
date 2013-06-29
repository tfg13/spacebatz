package de._13ducks.spacebatz.client.graphics.overlay.impl.inventory;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.overlay.TriggeredOverlay;
import de._13ducks.spacebatz.client.graphics.util.ContainerGUIElement;
import de._13ducks.spacebatz.client.graphics.util.DraggedImage;
import de._13ducks.spacebatz.server.data.Inventory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_DROP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE_ITEM;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;

/**
 * Overlay für das Inventar. Ermöglicht an- und ablegen von Items.
 *
 * @author mekhar
 */
public class InventoryOverlay extends TriggeredOverlay {

    /**
     * Das Item, das an der Maus angezeigt wird wenn man es per drag and drop bewegt.
     */
    private DraggedImage draggedItem;
    /**
     * Der Slot, von dem gedraggt wird *falls* gerade drag and drop stattfindet, -1 sonst.
     */
    private int dragStartSlot;
    /**
     * Gibt an, ob gerade drag and Drop ausgeführt wird.
     */
    private boolean dragging = false;
    private ContainerGUIElement container;
    private HashMap<Integer, InventoryItemSlot> slots;

    /**
     * Initialisiert das Overlay.
     */
    public InventoryOverlay() {
        init(new int[]{Keyboard.KEY_I}, true);
        container = new ContainerGUIElement();
        slots = new HashMap<>();


        int x, y, width, height;





        // Trash
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.95);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.95);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        InventoryTrashSlot trash = new InventoryTrashSlot(x, y, width, height, this, 2);
        slots.put(-1, trash);
        container.addChild(trash);

        // Rucksackslots:
        InventoryItemSlot slot;
        for (int column = 0; column < 10; column++) {
            for (int row = 0; row < 3; row++) {
                x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.25 + column * 0.05));
                y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.25 + row * 0.05));
                width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
                height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
                slot = new InventoryItemSlot(x, y, width, height, this, 10 * row + column);
                slots.put(10 * row + column, slot);
                container.addChild(slot);
            }
        }


        // Waffe 1:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.62));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.7));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.WEAPONSLOT1);
        slots.put(Inventory.WEAPONSLOT1, slot);
        container.addChild(slot);

        // Waffe 2:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.62));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.65));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.WEAPONSLOT2);
        slots.put(Inventory.WEAPONSLOT2, slot);
        container.addChild(slot);

        // Waffe 3:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.62));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.6));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.WEAPONSLOT3);
        slots.put(Inventory.WEAPONSLOT3, slot);
        container.addChild(slot);

        // Tool 1:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.62));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.55));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.TOOLSLOT1);
        slots.put(Inventory.TOOLSLOT1, slot);
        container.addChild(slot);

        // Tool 2:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.62));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.5));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.TOOLSLOT2);
        slots.put(Inventory.TOOLSLOT2, slot);
        container.addChild(slot);


        // Hut 1:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.33));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.7));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.HATSLOT1);
        slots.put(Inventory.HATSLOT1, slot);
        container.addChild(slot);

        // Hut 2:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.33));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.65));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.HATSLOT2);
        slots.put(Inventory.HATSLOT2, slot);
        container.addChild(slot);

        // Rüstungstyp 1:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.33));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.6));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.ARMOR1SLOT);
        slots.put(Inventory.ARMOR1SLOT, slot);
        container.addChild(slot);

        // Rüstungstyp 2:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.33));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.55));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.ARMOR2SLOT);
        slots.put(Inventory.ARMOR2SLOT, slot);
        container.addChild(slot);

        // Rüstungstyp 3:
        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * (0.33));
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * (0.5));
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        slot = new InventoryItemSlot(x, y, width, height, this, Inventory.ARMOR3SLOT);
        slots.put(Inventory.ARMOR3SLOT, slot);
        container.addChild(slot);


        // Drag and Drop Bild:
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.05);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.05);
        draggedItem = new DraggedImage(width, height, "item.png", 0, 32);
        container.addChild(draggedItem);



    }

    /**
     * Beginnt eine Drag and Drop Operation.
     *
     * @param dragStartSlot der Slot, von dem gedraggt wird
     * @param draggedItemTile die Tile des Items, das gedraggt wird
     */
    void startDrag(int dragStartSlot, int draggedItemTile) {
        this.dragStartSlot = dragStartSlot;
        draggedItem.setTile(draggedItemTile);
        draggedItem.isVisible = true;
        dragging = true;
    }

    /**
     * Beendet eine Drag and Drop Operation über einem Zielslot.
     *
     * @param dragEndSlot der Slot über dem das draggen beendet wurde.
     */
    void endDrag(int dragEndSlot) {
        if (dragging) {
            dragging = false;
            draggedItem.isVisible = false;
            if (dragStartSlot != dragEndSlot) {
                if (GameClient.player.inventory.tryMoveItem(dragStartSlot, dragEndSlot, GameClient.player.properties)) {
                    // Tiles vertauschen:
                    int tile1 = ((InventoryItemSlot) (slots.get(dragStartSlot))).getTile();
                    int tile2 = ((InventoryItemSlot) (slots.get(dragEndSlot))).getTile();
                    ((InventoryItemSlot) (slots.get(dragStartSlot))).setTile(tile2);
                    ((InventoryItemSlot) (slots.get(dragEndSlot))).setTile(tile1);
                    // Änderung an Client senden:
                    CTS_MOVE_ITEM.sendMoveItem(dragStartSlot, dragEndSlot);
                }
            }
        }
    }

    /**
     * Beendet eine Drag and Drop Operation über dem Mülleimer.
     */
    void endDragInTrash() {
        if (dragging) {
            dragging = false;
            draggedItem.isVisible = false;
            if (GameClient.player.inventory.tryDeleteItem(dragStartSlot, GameClient.player.properties)) {
                ((InventoryItemSlot) (slots.get(dragStartSlot))).setTile(-1);
                CTS_DROP_ITEM.sendDropItem(dragStartSlot);
            }
        }
    }

    @Override
    protected void keyboardInput(int key, boolean pressed) {
        container.keyboardInput(key, dragging);
    }

    @Override
    protected void mouseMove(int mx, int my) {
        container.mouseMove(mx, my);
    }

    @Override
    protected void mousePressed(int mx, int my, int button) {
        container.mousePressed(mx, my, button);
    }

    @Override
    protected void mouseReleased(int mx, int my, int button) {
        container.mouseReleased(mx, my, button);
        // jetzt ggf. noch laufende Drag and Drop Operation stoppen
        dragStartSlot = -1;
    }

    @Override
    protected void triggeredRender() {
        container.render();
    }

    public void createItem(int slot, Item item) {
        if (GameClient.player.inventory.tryCreateItem(item, GameClient.player.properties) != -1) {
            slots.get(slot).setTile(item.getPic());
        }

    }

    /**
     * Erzwingt die Angegebene Ordnung der Items im Inventar.
     *
     * @param data
     */
    public void forceMapping(byte[] data) {
        GameClient.player.inventory.forceInventoryMapping(data);
    }
}
