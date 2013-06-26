package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.PropertyList;

/**
 * Ein Slot für Items.
 *
 * @author mekhar
 */
public class ItemSlot {

    private Item item;
    private int[] acceptedItemClasses;
    /**
     * Gibt an ob dieser Slot zur Ausrüstung gehört und sein Item die Stats des Trägers verbessert.
     */
    public boolean isEquipSlot;

    public ItemSlot(int[] acceptedItemClasses, boolean isEquipSlot) {
        this.acceptedItemClasses = acceptedItemClasses;
        this.isEquipSlot = isEquipSlot;
    }

    public boolean isEmpty() {
        return item == null;
    }

    public boolean canEquipClass(int itemClass) {
        for (int type : acceptedItemClasses) {
            if (type == itemClass) {
                return true;
            }
        }
        return false;
    }

    public Item getItem() {
        return item;
    }

    public Item removeItem(PropertyList props) {
        if (!isEmpty()) {
            Item ret = item;
            if (isEquipSlot) {
                props.removeProperties(item.getBonusProperties());
            }
            item = null;
            return ret;
        } else {
            throw new IllegalStateException("Slot ist leer, kann kein Item entfernen!");
        }
    }

    public void setItem(Item item, PropertyList props) {
        if (isEmpty()) {
            if (canEquipClass(item.getItemClass())) {
                if (isEquipSlot) {
                    props.addProperties(item.getBonusProperties());
                }
                this.item = item;
            } else {
                throw new IllegalArgumentException("Item kann nicht in diesen Slottyp gelegt werden!");
            }
        } else {
            throw new IllegalArgumentException("Slot ist belegt!");
        }
    }
}
