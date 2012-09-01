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

import com.rits.cloning.Cloner;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import java.io.Serializable;
import java.util.Objects;

/**
 * Items können Attribute besitzen, die jeweils einen oder mehrere Itemwerte verändern.
 *
 * Attribute bestehen aus Itemwerten, die nur das Item angehen (z.B. auch Qualität) und Bonuswerten, die den Träger
 * betreffen.
 *
 * @author Jojo
 */
public class ItemAttribute implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Name des Attributs
     */
    private String name;
    /**
     * Die Bonuswerte, die dieses Attribut gibt. z.B. +10 Hitpoints
     */
    public PropertyList bonusStats;
    private int quality, itemClass, pic, amount;
    /**
     * Die Eigenschaften der Waffe, z.B. Schaden
     */
    public PropertyList weaponStats;
    /**
     * Die Fähigkeit, die dieses ItemAttribut gibt (null wenn das Item keine Fähigkeiten gibt)
     */
    private transient Ability weaponAbility;

    /**
     * Erstellt ein neues Attribut mit Bonuswerten.
     *
     * @param name der Name des Attributs
     * @param stats die initalisierungs-stats
     *
     */
    public ItemAttribute(String name, PropertyList stats) {
        super();
        this.name = name;
        bonusStats = new PropertyList();

        weaponStats = new PropertyList();
    }

    /**
     * Erstellt eine neues Attribut ohne Werte.
     *
     * @param name der Name des Attributs
     */
    public ItemAttribute(String name) {
        super();
        this.name = name;
        bonusStats = new PropertyList();

        weaponStats = new PropertyList();
    }

    /**
     * Gibt den Namen dieses Attributs zurück.
     *
     * @return der Name des Attributs
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt die Bonus-Stats ideses Attributs zurück.
     *
     * @return die BonusStats, die dieses Attrbut gibt
     */
    public PropertyList getBonusStats() {
        return bonusStats;
    }

    /**
     * Gibt die Waffen-Stats dieses Attributs zurück.
     *
     * @return die WaffenStats, die dieses Attrbut der Waffe gibt
     */
    public PropertyList getWeaponStats() {
        return weaponStats;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemAttribute) {
            ItemAttribute i = (ItemAttribute) o;
            return i.name.equals(this.name);
        }
        return false;
    }

    /**
     * Gibt eine neue Instanz der Fähigkeit, die dieses Attribut gewährt zurück, oder null wenn dieses Attribut keine
     * Fähigkeit gewährt.
     *
     * @return die Fähigkeit, die dieses ItemAttribut gibt oder null
     */
    public Ability getNewWeaponAbilityInstance() {
        Cloner cloner = new Cloner();
        return cloner.deepClone(weaponAbility);
    }

    /**
     * Die Fähigkeit, die dieses ItemAttribut gibt (null wenn das Item keine Fähigkeiten gibt)
     *
     * @param weaponAbility the weaponAbility to set
     */
    public void setWeaponAbility(Ability weaponAbility) {
        this.weaponAbility = weaponAbility;
    }

    /**
     * @return the quality
     */
    public int getQuality() {
        return quality;
    }

    /**
     * @return the itemClass
     */
    public int getItemClass() {
        return itemClass;
    }

    /**
     * @return the pic
     */
    public int getPic() {
        return pic;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * @param itemClass the itemClass to set
     */
    public void setItemClass(int itemClass) {
        this.itemClass = itemClass;
    }

    /**
     * @param pic the pic to set
     */
    public void setPic(int pic) {
        this.pic = pic;
    }

    void setAmount(int i) {
        amount = i;
    }

    public int getAmount() {
        return amount;
    }
}
