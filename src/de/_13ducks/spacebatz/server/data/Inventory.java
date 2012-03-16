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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;
import java.util.HashMap;

/**
 * Das Inventar eines Clients, serverseitig
 * @author der Nikolaus
 */
public class Inventory {

    /**
     * Items im Inventar
     */
    private HashMap<Integer, Item> items;
    /**
     * Wieviel Geld im Inventar ist
     */
    private int money;

    public Inventory() {
        items = new HashMap<>();
    }

    /**
     * @return the items
     */
    public HashMap<Integer, Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(HashMap<Integer, Item> items) {
        this.items = items;
    }

    /**
     * @param items the items to add
     */
    public void putItem(int netID, Item item) {
        this.items.put(netID, item);
        if (item.getStats().itemStats.get("itemclass") == 0) {
            setMoney(getMoney() + (int) item.getStats().itemStats.get("amount"));
        }
    }

    /**
     * @return the money
     */
    public int getMoney() {
        return money;
    }

    /**
     * @param money the money to set
     */
    public void setMoney(int money) {
        this.money = money;
    }
}
