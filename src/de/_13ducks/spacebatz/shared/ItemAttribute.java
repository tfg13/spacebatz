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

import java.io.Serializable;

/**
 * Items können Attribute besitzen, die jeweils einen oder mehrere Itemwerte verändern.
 *
 * Attribute bestehen aus Itemwerten, die nur das Item angehen (z.B. auch Waffenstats) und Bonuswerten, die den
 * Träger betreffen.
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
     * Die Bonuswerte, die dieses Attribut gibt.
     * z.B. +10 Hitpoints
     */
    private Properties bonusStats;
    /**
     * Die Itemwerte, die dieses Attribut gibt.
     * z.B. +10 Schaden für eine Waffe oder Qualität 1
     */
    private Properties itemStats;

    /**
     * Erstellt ein neues Attribut mit Bonuswerten.
     *
     * @param name der Name des Attributs
     * @param stats die initalisierungs-stats
     */
    public ItemAttribute(String name, Properties stats) {
        super();
        this.name = name;
        bonusStats = new Properties();
        itemStats = new Properties();
    }

    /**
     * Erstellt eine neues Attribut ohne Werte.
     *
     * @param name der Name des Attributs
     */
    public ItemAttribute(String name) {
        super();
        this.name = name;
        bonusStats = new Properties();
        itemStats = new Properties();
    }

    /**
     * Gibt den Namen dieses Attributs zurück.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt den Wert einer Bonus-Eigenschaft zurück.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getBonusProperty(String name) {
        return bonusStats.getProperty(name);
    }

    /**
     * Setzt den Wert einer Bonus-Eigenschaft
     *
     * Beispiel: setBonusProperty("hitpoints",10) sorgt dafür dass das Attribut 10 hp dazugibt.
     *
     * @param name der Name der Eigenschaft, der gesetzt werden soll
     * @param value der Wert, auf den die Eigenschaft gesetzt werden soll
     */
    final public void setBonusProperty(String name, double value) {
        bonusStats.setProperty(name, value);
    }

    /**
     * Gibt den Wert einer Item-Eigenschaft zurück.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getItemProperty(String name) {
        return itemStats.getProperty(name);
    }

    /**
     * Setzt den Wert einer Item-Eigenschaft
     *
     * Beispiel: setItemProperty("damage",10) sorgt dafür dass das Item mit dem Attribut 10 schaden mehr anrichtet
     * (Wenn es eine Waffe ist)
     *
     * @param name der Name der Eigenschaft, der gesetzt werden soll
     * @param value der Wert, auf den die Eigenschaft gesetzt werden soll
     */
    final public void setItemProperty(String name, double value) {
        itemStats.setProperty(name, value);
    }

    /**
     * Gibt die Bonus-Stats ideses Attributs zurück.
     *
     * @return die BonusStats, die dieses Attrbut gibt
     */
    public Properties getBonusStats() {
        return bonusStats;
    }

    /**
     * Gibt die Weapos-Stats, die dieses Attribut gibt, zurück.
     *
     * @return die Waffen-Stats dieses Attributs
     */
    public Properties getItemStats() {
        return itemStats;
    }
}
