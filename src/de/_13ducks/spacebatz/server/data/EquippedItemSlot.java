package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;

/**
 * Ein Slot zum Item-Anlegen
 * @author Jojo
 */
public class EquippedItemSlot {

    private int slotid;
    private Item item;

    /*
     * Konstruktor, kriegt ne int die bestimmt, welche Itemart reingetan werden darf
     */
    public EquippedItemSlot(int slotid) {
        this.slotid = slotid;
    }

    /**
     * @return the slotid
     */
    public int getSlotid() {
        return slotid;
    }

    /**
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Item item) {
        this.item = item;
    }
}
