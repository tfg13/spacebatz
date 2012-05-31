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
import de._13ducks.spacebatz.server.data.abilities.Ability;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Ein Item, dass auf der Map oder in einem Inventar liegt
 *
 * @author Jojo
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
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
     * Der Name ds Items
     */
    private String name;
    /**
     * Die Eigenschaften dieses Items
     */
    private PropertyList itemProperties;
    /**
     * Die Boni, die das Item dem Träger gibt
     */
    private PropertyList bonusProperties;
    /**
     * Die Fähigkeit, falls das Item eine Waffe ist
     */
    private transient Ability weaponAbility;

    /**
     * Erzeugt ein neues Item
     *
     * @param name der Name des Items
     * @param posX
     * @param posY
     * @param baseAttribute
     * @param netID
     */
    public Item(String name, ItemAttribute baseAttribute, double posX, double posY, int netID) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.netID = netID;
        this.amount = 1;
        itemProperties = new PropertyList();
        bonusProperties = new PropertyList();
        itemAttributes = new ArrayList<>();
        weaponAbility = baseAttribute.getNewWeaponAbilityInstance();

        // die boni des Grundattributs addieren:
        addAttribute(baseAttribute);


    }

    /**
     * Gibt dem Item ein Attribut dazu.
     *
     * @param itemAttribute das Attribut, das das Item bekommen soll
     */
    public void addAttribute(ItemAttribute itemAttribute) {
        // Das Attribut in die Liste aufnehmen:
        itemAttributes.add(itemAttribute);
        // Die Bonus-Werte des Attributs zu den ItemProperties addieren:
        bonusProperties.addProperties(itemAttribute.getBonusStats());
        // Die Item-Werte des Attributs hinzufügen:
        itemProperties.addProperties(itemAttribute.getItemStats());
        if (weaponAbility != null) {
            // Die Waffenstats der Waffenfähigkeit geben, wenn dies eine Waffe ist:
            weaponAbility.addProperties(itemAttribute.getWeaponStats());

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
    public ArrayList<ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    /**
     * Gibt den Namen des Items zurück
     *
     * @return der Name des Items
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Namen des Items
     *
     * @param name der Name den das Item haben soll
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gibt den Wert einer Item-Eigenschaft zurück. Wenn die Eigenschaft nicht initialisiert wurde, wird 0
     * zurückgegeben.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    public double getItemProperty(String name) {
        return itemProperties.getProperty(name);
    }

    /**
     * Gibt die Boni des Items zurück
     *
     * @return die Boni des Items zurück
     */
    public PropertyList getBonusProperties() {
        return bonusProperties;
    }

    /**
     * Die Fähigkeit, falls das Item eine Waffe ist
     *
     * @return the weaponAbility
     */
    public Ability getWeaponAbility() {
        return weaponAbility;
    }
}
