package de._13ducks.spacebatz.server.data;

/**
 * Verwaltet Slots für die Items, die der Client gerade angelegt hat
 * @author Jojo
 */
public class EquippedItems {

    EquippedItemSlot[] eqslot = new EquippedItemSlot[4];
    
    public EquippedItems() {
        for (int i = 0; i < 3; i++) {
            EquippedItemSlot weaponslot = new EquippedItemSlot(0);
            eqslot[i] = weaponslot;
        }
        
        EquippedItemSlot armorslot = new EquippedItemSlot(1);
        eqslot[3] = armorslot;
    }
}
