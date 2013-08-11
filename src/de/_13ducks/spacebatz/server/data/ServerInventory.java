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
        for (int i = CompileTimeParameters.INVENTORY_SIZE; i < CompileTimeParameters.INVENTORY_SIZE + EQUIPSLOT_COUNT - 1; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, owner.getProperties());
                return i;
            }
        }
        // dann Slots im Rucksack überprüfen:
        for (int i = 0; i < CompileTimeParameters.INVENTORY_SIZE - 1; i++) {
            if (slots.get(i).canEquipClass(item.getItemClass()) && slots.get(i).isEmpty()) {
                slots.get(i).setItem(item, owner.getProperties());
                return i;
            }
        }
        return -1;
    }

    /**
     * Codiert die Inventarbelegung in ein byte[).
     *
     * Das erste byte ist die Zahl der Einträge. Jeder Eintrag besteht aus einem byte das den slot angibt, und einem Integer (4 bytes) der die netId des zugehörigen Items angibt.
     *
     * @return das Byte-Array mit der Inventarbelegung
     */
    public byte[] getInventoryMapping() {
        int count = 0;
        BitEncoder encoder = new BitEncoder();
        encoder.writeByte((byte) 0); // Platzhalter für die Länge
        for (int i = 0; i < slots.size(); i++) {
            encoder.writeByte((byte) i);
            if (!slots.get(i).isEmpty()) {
                encoder.writeInt(slots.get(i).getItem().getNetID());
                count++;
            }
        }
        byte[] data = encoder.getBytes();
        data[0] = (byte) count;
        return data;
    }

    public Item getItem(int slot) {
        return slots.get(slot).getItem();
    }
}
