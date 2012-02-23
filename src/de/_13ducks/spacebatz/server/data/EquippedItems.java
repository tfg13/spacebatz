package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;

/**
 * Verwaltet Slots für die Items, die der Client gerade angelegt hat
 * @author Jojo
 */
public class EquippedItems {
    /**
     * Enthält einzelne Slotarten, z.B. die Waffenslots, Armorslots
     */
    private Item[][] equipslots = new Item[3][];
    
    public EquippedItems() {
        Item[] wslot = new Item[3];
        Item[] aslot = new Item[1];
        
        equipslots[1] = wslot;
        equipslots[2] = aslot;
    }

    public Item[][] getEquipslots() {
        return equipslots;
    }
}
