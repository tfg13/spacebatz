package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.ItemAttribute;
import de._13ducks.spacebatz.ItemTypeStats;
import java.util.ArrayList;

/**
 *
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
    /*
     * Ort, an dem das Item erstellt wurde
     */
    private double posX;
    private double posY;
    /**
     * Attribute des Items
     */
    private ArrayList<ItemAttribute> itemattributes;

    public Item(double posX, double posY, ItemTypeStats stats, int netID) {
        this.posX = posX;
        this.posY = posY;
        this.stats = stats;
        this.netID = netID;
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
}
