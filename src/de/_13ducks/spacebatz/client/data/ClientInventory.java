package de._13ducks.spacebatz.client.data;

import static de._13ducks.spacebatz.server.data.Inventory.EQUIPSLOT_COUNT;
import de._13ducks.spacebatz.server.data.entities.ItemSlot;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.PropertyList;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import java.util.HashMap;

/**
 * Clientinventar. Falls eine Kollision auftritt wird das kollidiernde Item zwischengespeichert bis der Server die korrekte Belegung schickt.
 *
 * @author mekhar
 */
public class ClientInventory extends de._13ducks.spacebatz.server.data.Inventory {

    public int tryCreateItem(Item item, PropertyList props) {

        // erst Ausrüstungsslots durchsuchen:
        for (int i = CompileTimeParameters.INVENTORY_SIZE; i < CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, props);
                return i;
            }
        }
        // dann Slots im Rucksack überprüfen:
        for (int i = 0; i < CompileTimeParameters.INVENTORY_SIZE; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, props);
                return i;
            }
        }


        // Item in temporären slot stecken:
        for (int i = CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT; i < Integer.MAX_VALUE; i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, new ItemSlot(new int[]{0, 1, 2, 3, 4, 5}, false));
                slots.get(i).setItem(item, props);
                return -1;
            } else {
                if (slots.get(i).isEmpty()) {
                    slots.get(i).setItem(item, props);
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * Orndet die Items so an wie in mapping angegeben.
     *
     * @param mapping
     */
    public void forceInventoryMapping(byte[] mapping, PropertyList props) {
        // Alle Items temporär zwischenspeichern:
        HashMap<Integer, Item> tempItems = new HashMap<>();
        for (ItemSlot slot : slots.values()) {
            if (!slot.isEmpty()) {
                tempItems.put(slot.getItem().getNetID(), slot.getItem());
                slot.removeItem(props);
            }
        }

        // Items nach der Vorgabe vom Server wieder einfügen:
        BitDecoder decoder = new BitDecoder(mapping);
        for (int slot = 0; slot < CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT; slot++) {
            int itemNetID = decoder.readInt();
            if (itemNetID != -1) {
                if (tempItems.containsKey(itemNetID)) {
                    slots.get(slot).setItem(tempItems.get(itemNetID), props);
                } else {
                    throw new IllegalStateException("Client misses Item " + itemNetID);
                }
            }
        }
    }

    public Item getItem(int slot) {
        return slots.get(slot).getItem();
    }
}
