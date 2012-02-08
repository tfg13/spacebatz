/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.Item;

/**
 * Jedes Item im Spielerinventar kriegt so eins zur Verwaltung
 * Achtung: leere Slots im Inventar kriegen dieses Objekt (zur Zeit) nicht.
 * @author Jojo
 */
public class InventorySlot {
    /**
     * zugehöriges Item
     */
    private Item item;
    /**
     * Menge, falls mehrere Items des selben Typs gestackt werden können
     */
    private int amount;
    
    public InventorySlot(Item item) {
        this.item = item;
        this.amount = 1;
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

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
