/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.Item;

/**
 * Jedes Item im Spielerinventar kriegt so eins zur Verwaltung
 * Achtung: leere Slots im Inventar kriegen dieses Objekt nicht.
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
