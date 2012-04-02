package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;

/**
 * Ein Itemträger, kann Items tragen und ausrüsten.
 * Kann im moment beliebig viele Items aufnehmen und ausrüsten.
 *
 * @author michael
 */
public class ItemCarrier extends AbilityUser {

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

    }

    public void collectItemToInventory(Item item) {
    }

    public void dropItemFromInventory(Item item) {
    }

    public void equipItem(Item item, int Slot) {
    }

    public void dropEquipedItem(Item item) {
    }

    public void unequipItemToInventory(Item item) {
    }

    public void selectActiveWeapon(int slot) {
    }
}
