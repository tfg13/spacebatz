package de._13ducks.spacebatz.server.data;

import static de._13ducks.spacebatz.server.data.Inventory.EQUIPSLOT_COUNT;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.BitEncoder;

/**
 * Das Serverinventar. Kann die Inventarbelegung berechnen.
 *
 * @author mekhar
 */
public class ServerInventory extends Inventory {

    /**
     * Legt das Item in den ersten freien Slot der es aufnehmen kann.
     *
     * @param item das Item
     * @return die id des slots in den das Item gelegt wurde oder -1 wenn kein platz war.
     */
    public int tryCreateItem(Item item, Char owner) {
        // erst Ausrüstungsslots durchsuchen:
        for (int i = CompileTimeParameters.INVENTORY_SIZE; i < CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, owner.getProperties());
                return i;
            }
        }
        // dann Slots im Rucksack überprüfen:
        for (int i = 0; i < CompileTimeParameters.INVENTORY_SIZE; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, owner.getProperties());
                return i;
            }
        }
        return -1;
    }

    /**
     * Codiert die Inventarbelegung in ein byte-Array.
     *
     * Das Byte-Array enthält einen Integer ( die netId des Items ) für jeden slot, also insgesamt CompileTimeParameters.INVENTORY_SIZE Einträge.
     *
     * @return das Byte-Array mit der Inventarbelegung
     */
    public byte[] getInventoryMapping() {
        BitEncoder encoder = new BitEncoder();
        for (int i = 0; i < CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT; i++) {
            if (!slots.get(i).isEmpty()) {
                encoder.writeInt(slots.get(i).getItem().getNetID());
            } else {
                encoder.writeInt(- 1);
            }
        }
        return encoder.getBytes();
    }

    public Item getItem(int slot) {
        return slots.get(slot).getItem();
    }
}
