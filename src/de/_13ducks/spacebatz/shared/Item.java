package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.client.InventorySlot;
import java.util.ArrayList;

/**
 * Ein Item, dass auf der Map oder in einem Inventar liegt
 * @author Jojo
 */
public class Item implements java.io.Serializable {

    public ItemTypeStats stats;
    /**
     * Typ des Items.
     */
    public byte itemTypeID;
    /**
     * Die ID des Items.
     */
    public int netID;
    /**
     * Menge des Items, wichtig bei stackbaren Materialien / Geld
     */
    private int amount;
    /*
     * Ort, an dem das Item erstellt wurde
     */
    private double posX;
    private double posY;
    /**
     * Attribute des Items
     */
    private ArrayList<ItemAttribute> itemattributes;
    /**
     * Inventarplatz des Items, nur für Client wichtig
     */
    private InventorySlot inventoryslot;

    public Item(double posX, double posY, ItemTypeStats stats, int netID) {
        this.posX = posX;
        this.posY = posY;
        this.stats = stats;
        this.netID = netID;
        this.amount = 1;
        itemattributes = new ArrayList<>();
    }

    /**
     * @return the posX
     */
    public double getPosX() {
        return posX;
    }

    /**
     * @return the posY
     */
    public double getPosY() {
        return posY;
    }

    /**
     * @return the itemattributes
     */
    public ArrayList<ItemAttribute> getItemattributes() {
        return itemattributes;
    }

    /**
     * @param itemattributes the itemattributes to set
     */
    public void setItemattributes(ArrayList<ItemAttribute> itemattributes) {
        this.itemattributes = itemattributes;
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

    /**
     * @return the inventoryslot
     */
    public InventorySlot getInventoryslot() {
        return inventoryslot;
    }

    /**
     * @param inventoryslot the inventoryslot to set
     */
    public void setInventoryslot(InventorySlot inventoryslot) {
        this.inventoryslot = inventoryslot;
    }
}
