package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.entities.ItemSlot;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.PropertyList;
import java.util.HashMap;

/**
 * Verwaltet Ausrüstung und Items eines Spielers. Jeder slot hat einen Index: die Slots 0 bis CompiletimeParameters.INVENTORY_SIZE sind Rucksackslots, die mit den unten definierten
 * konstanten sind Ausrüstungsslots.
 *
 * @author mekhar
 */
public abstract class Inventory {

    public static final int EQUIPSLOT_COUNT = 10;
    public static final int WEAPONSLOT1 = CompileTimeParameters.INVENTORY_SIZE + 0;
    public static final int WEAPONSLOT2 = CompileTimeParameters.INVENTORY_SIZE + 1;
    public static final int WEAPONSLOT3 = CompileTimeParameters.INVENTORY_SIZE + 2;
    public static final int TOOLSLOT1 = CompileTimeParameters.INVENTORY_SIZE + 3;
    public static final int TOOLSLOT2 = CompileTimeParameters.INVENTORY_SIZE + 4;
    public static final int HATSLOT1 = CompileTimeParameters.INVENTORY_SIZE + 5;
    public static final int HATSLOT2 = CompileTimeParameters.INVENTORY_SIZE + 6;
    public static final int ARMOR1SLOT = CompileTimeParameters.INVENTORY_SIZE + 7;
    public static final int ARMOR2SLOT = CompileTimeParameters.INVENTORY_SIZE + 8;
    public static final int ARMOR3SLOT = CompileTimeParameters.INVENTORY_SIZE + 9;
    /**
     * Die Inventarslots
     */
    protected HashMap<Integer, ItemSlot> slots;
    /**
     * Die aktive Waffe
     */
    private int activeWeaponSlot = WEAPONSLOT1;

    public Inventory() {
        slots = new HashMap<>();

        // Rucksackslots für alle Itemarten:

        for (int i = 0; i < CompileTimeParameters.INVENTORY_SIZE; i++) {
            slots.put(i, new ItemSlot(new int[]{1, 2, 3, 4, 5, 6}, false));
        }

        // 3 Waffenslots:
        slots.put(WEAPONSLOT1, new ItemSlot(new int[]{1}, true));

        slots.put(WEAPONSLOT2, new ItemSlot(new int[]{1}, true));
        slots.put(WEAPONSLOT3, new ItemSlot(new int[]{1}, true));

        // 2 Toolslots:
        slots.put(TOOLSLOT1, new ItemSlot(new int[]{6}, true));
        slots.put(TOOLSLOT2, new ItemSlot(new int[]{6}, true));

        // 2 Hüte:
        slots.put(HATSLOT1, new ItemSlot(new int[]{2}, true));
        slots.put(HATSLOT2, new ItemSlot(new int[]{2}, true));

        // Rüstung 1:
        slots.put(ARMOR1SLOT, new ItemSlot(new int[]{3}, true));

        // Rüstung 2:
        slots.put(ARMOR2SLOT, new ItemSlot(new int[]{4}, true));

        // Rüstung 3:
        slots.put(ARMOR3SLOT, new ItemSlot(new int[]{5}, true));

    }

    /**
     * Versucht, ein Item von einem Slot in einen anderen zu bewegen.
     *
     * @param fromSlot Ausgangsslot
     * @param toSlot Zielslot
     * @return true bei Erfolg, false wenn das Item nicht bewegt werden kann.
     */
    public boolean tryMoveItem(int fromSlot, int toSlot, PropertyList props) {
        ItemSlot from = slots.get(fromSlot);
        ItemSlot to = slots.get(toSlot); // Ist da überhaupt ein Item das verschoben werden kann?
        if (!from.isEmpty()) {
            Item item = from.getItem();
            // Passt das Item in den Zielslot?
            if (to.canEquipClass(item.getItemClass())) {
                if (to.isEmpty()) {
                    // Item wird in einen leeren slot geschoben:
                    to.setItem(item, props);
                    from.removeItem(props);
                    return true;
                } else {
                    // zwei Items werden vertauscht:
                    Item item2 = to.getItem();
                    // Passt das zweite Item in den slot des ersten?
                    if (from.canEquipClass(item2.getItemClass())) {
                        to.removeItem(props);
                        from.removeItem(props);
                        to.setItem(item, props);
                        from.setItem(item2, props);
                        return true;
                    } else {
                        System.out.println("Client tried to swap incompatible Items");
                        return false;
                    }
                }
            } else {
                System.out.println("Client tried to move item to a slot of the wrong class!");
                return false;
            }
        } else {
            System.out.println("Client tried to move item from empty slot ( " + fromSlot + ").");
            return false;
        }
    }

    /**
     * Löshct das Item im angegebenen Slot.
     *
     * @param slot
     * @return true bei Erfolg, false wenn der Slot leer war.
     */
    public boolean tryDeleteItem(int slot, PropertyList props) {
        if (!slots.get(slot).isEmpty()) {
            slots.get(slot).removeItem(props);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the activeWeapon
     */
    public Item getActiveWeapon() {
        return slots.get(activeWeaponSlot).getItem();
    }

    /**
     * @param activeWeapon the activeWeapon to set
     */
    public void setActiveWeapon(int slot) {
        if (slot == Inventory.WEAPONSLOT1 || slot == Inventory.WEAPONSLOT2 || slot == Inventory.WEAPONSLOT3) {
            activeWeaponSlot = slot;
        }
    }

    public int getActiveWeaponSlot() {
        return activeWeaponSlot;
    }
}
