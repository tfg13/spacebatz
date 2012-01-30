package de._13ducks.spacebatz.server.data;

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

    /**
     * @param money the money to add
     */
    public void addMoney(int money) {
        this.money += money;
        System.out.println("money " + this.money);
    }
}
