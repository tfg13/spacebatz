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
package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.client.InventorySlot;
import java.util.ArrayList;

/**
 * Ein Item, dass auf der Map oder in einem Inventar liegt
 *
 * @author Jojo
 */
public class Item implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private ItemTypeStats stats;
    /**
     * Die netID des Items.
     */
    private int netID;
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
     * @param posX the posX to set
     */
    public void setPosX(double posX) {
        this.posX = posX;
    }

    /**
     * @param posY the posY to set
     */
    public void setPosY(double posY) {
        this.posY = posY;
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

    /**
     * @return the netID
     */
    public int getNetID() {
        return netID;
    }

    /**
     * @return the stats
     */
    public ItemTypeStats getStats() {
        return stats;
    }
}
