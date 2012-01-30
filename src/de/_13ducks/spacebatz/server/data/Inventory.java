package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Item;
import java.util.ArrayList;

/**
 * Das Inventar eines Clients, serverseitig
 * @author der Nikolaus
 */
public class Inventory {

    /**
     * Liste aller Items
     */
    private ArrayList<Item> items;
    /**
     * Wieviel Geld im Inventar ist
     */
    private int money;

    public Inventory() {
        items = new ArrayList<>();
    }

    /**
     * @return the items
     */
    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    /**
     * @param items the items to add
     */
    public void addItem(Item item) {
        this.items.add(item);
        if (item.stats.itemStats.get("itemclass") == 0) {
            money += (int) item.stats.itemStats.get("amount");
        }
    }
}
