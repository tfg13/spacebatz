package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;
import java.util.HashMap;

/**
 * Das Inventar eines Clients, serverseitig
 * @author der Nikolaus
 */
public class Inventory {

    /**
     * Hashmap, weist netID Item zu
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
        if (item.stats.itemStats.get("itemclass") == 0) {
            setMoney(getMoney() + (int) item.stats.itemStats.get("amount"));
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
