package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;
import java.util.ArrayList;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten.
 * Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends AbilityUser {

    /**
     * Der Rucksack, Lagerplatz für Items
     */
    private ArrayList<Item> backpack;
    /**
     * Die ausgerüsteten Items
     */
    private ArrayList<Item> equipedItems;

    /**
     * Erzeugt einen neuen ItemCarrier
     *
     * @param posX die X-Koordinate des Carriers
     * @param posY die Y-Koordinate des Carriers
     * @param netId die netId des Carriers
     * @param typeId die typeId des Carriers
     */
    public ItemCarrier(double posX, double posY, int netId, byte typeId) {
        super(posX, posY, netId, typeId);
        backpack = new ArrayList<>();
        equipedItems = new ArrayList<>();
    }

    /**
     * Rüstet ein Item aus dem backpack aus.
     * Wendet die Boni des Items auf die Eigenschaften des Chars an.
     *
     * @param netId die netId des Items das ausgerüstet werden soll
     */
    final protected void equipItem(int netId) {
        Item item = getItemFromBackpackByNetId(netId);
        if (item == null) {
            throw new IllegalArgumentException("Item with netId " + item.getNetID() + " is not in backpack!");
        } else {
            equipedItems.add(item);
            backpack.remove(item);
            // TODO: apply item stats to char stats
        }
    }

    /**
     * Legt ein Item von der Ausrüstung ab und platziert es im Inventar.
     * Entfernt die Boni des Items wieder von den Char-Eigenschaften.
     *
     * @param netId die netID des Items, das abgelegt werden soll
     */
    final protected void deQuipItem(int netId) {
        Item item = getItemFromEquipByNetId(netId);
        if (item == null) {
            throw new IllegalArgumentException("Item with netId " + netId + " is not equiped!");
        } else {
            equipedItems.remove(item);
            backpack.add(item);
            // TODO remove item bonuses
        }
    }

    /**
     * Sammelt ein Item ein und platziert es im Backpack.
     *
     * @param item das Item das aufgesammelt werden soll
     */
    final public void collectItem(Item item) {
        backpack.add(item);
    }

    /**
     * Entfernt ein Item aus dem Backpack
     *
     * @param netId die netID des Items das weggeworfen werden soll
     * @return the Item that was removed
     */
    final protected Item removeItemFromBackpack(int netId) {
        Item item = getItemFromBackpackByNetId(netId);
        if (item == null) {
            throw new IllegalArgumentException("There is no item with netId " + netId + " in backpack!");
        } else {
            backpack.remove(item);
            return item;
        }
    }

    /**
     * Gibt das Item mit der angegebenen netId aus dem Backpack zurück oder null wenn keines diese netId hat.
     *
     * @param netId die zu suchende netId
     * @return das entsprechende Item oder null wenns keins gibt
     */
    private Item getItemFromBackpackByNetId(int netId) {
        for (Item item : backpack) {
            if (item.getNetID() == netId) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gibt das Item mit der angegebenen netId aus den ausgerüsteten Items zurück oder null wenn keines diese netId hat.
     *
     * @param netId die zu suchende netId
     * @return das entsprechende Item oder null wenns keins gibt
     */
    private Item getItemFromEquipByNetId(int netId) {
        for (Item item : equipedItems) {
            if (item.getNetID() == netId) {
                return item;
            }
        }
        return null;
    }
}
