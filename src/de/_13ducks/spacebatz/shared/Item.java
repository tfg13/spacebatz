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
public class Item extends Properties {

    private static final long serialVersionUID = 1L;
    /**
     * Die Grundwerte für dieses Item.
     */
    private ItemAttribute baseAttribute;
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
    private ArrayList<ItemAttribute> itemAttributes;
    /**
     * Inventarplatz des Items, nur für Client wichtig
     */
    private InventorySlot inventoryslot;

    /**
     * Erzeugt ein neues Item
     *
     * @param posX
     * @param posY
     * @param baseAttribute
     * @param netID
     */
    public Item(double posX, double posY, ItemAttribute baseAttribute, int netID) {
        this.posX = posX;
        this.posY = posY;
        this.baseAttribute = baseAttribute;
        this.netID = netID;
        this.amount = 1;
        itemAttributes = new ArrayList<>();

    }

    /**
     * Gibt dem Item ein Attribut.
     *
     * @param itemAttribute das Attribut, das das Item bekommen soll
     */
    public void addAttribute(ItemAttribute itemAttribute) {
        // Das Attribut in die Liste aufnehmen:
        itemAttributes.add(itemAttribute);
        // Die Bonus-Werte des Attributs zu den ItemProperties addieren:
        addProperties(itemAttribute);
    }

    /**
     * Entfernt ein Attribut vom Item.
     *
     * @param itemAttribute das Attribut, entfernt werden soll
     */
    public void removeAttribute(ItemAttribute itemAttribute) {
        if (itemAttributes.contains(itemAttribute)) {
            // Das Attribut von der Liste entfernen:
            itemAttributes.remove(itemAttribute);
            // Die Bonus-Werte des Attributs wieder entfernen:
            removeProperties(itemAttribute);
        } else {
            throw new IllegalArgumentException("Dieses Item hat kein \"" + itemAttribute.getName() + "\"-Attribut!");
        }
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
     * Gibt die Liste der Itemattribute zurück.
     *
     * @return die Liste der ItemAttribute.
     */
    public Object getItemattributes() {
        return itemAttributes;
    }
}
