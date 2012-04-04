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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.shared.Properties;

/**
 * Chars sind Entities, die eine dynamische Liste von Eigenschaften wie Hitpoints, Rüstung etc haben.
 *
 * @author michael
 */
public abstract class Char extends AbilityUser {

    /**
     * Die Eigenschaften des Chars (Hitpoints, Rüstung etc).
     */
    private Properties properties;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x X-Koordinatedes Chars
     * @param y Y-Koordinatedes Chars
     * @param netID die netID des Chars
     * @param entityTypeID die typeID des Chars
     */
    public Char(double x, double y, int netID, byte entityTypeID) {
        super(x, y, netID, entityTypeID);
        properties = new Properties();

        // PictureID setzen:
        setProperty("pictureId", 0);
        setProperty("size", Settings.CHARSIZE);
        setProperty("hitpoints", Settings.CHARHEALTH);
        setProperty("damage", 1);
        setProperty("range", 10.0);
        setProperty("sightrange", 10.0);
        setProperty("attackcooldown", 60.0);
    }

    /**
     * Gibt den Wert einer Eigenschaft zurück.
     * Wenn die Eigenschaft nicht initialisiert wurde, wird 0 zurückgegeben.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Inkrementiert den Wert einer Eigenschaft.
     *
     * @param name der Name der Eigenschaft, der inkrementiert werden soll
     * @param value der Wert, um den die Eigenschaft inkrementiert werden soll
     * @return der neue Wert der Eigenschaft
     */
    final public double incrementProperty(String name, double value) {
        properties.incrementProperty(name, value);
        refreshAbilities();
        return properties.getProperty(name);

    }

    /**
     * Dekrementiert den Wert einer Eigenschaft.
     *
     * @param name der Name der Eigenschaft, der dekrementiert werden soll
     * @param value der Wert, um den die Eigenschaft dekrementiert werden soll
     * @return der neue Wert der Eigenschaft
     */
    final public double decrementProperty(String name, double value) {
        properties.decrementProperty(name, value);
        refreshAbilities();
        return properties.getProperty(name);
    }

    /**
     * Setzt den Wert einer Eigenschaft.
     *
     * @param name der Name der Eigenschaft, der gesetzt werden soll
     * @param value der Wert, auf den die Eigenschaft gesetzt werden soll
     */
    final public void setProperty(String name, double value) {

        properties.setProperty(name, value);
        refreshAbilities();
    }

    /**
     * Addiert alle Eigenschaften der angegebenen Properties zu den Properties des Chars.
     *
     * @param otherProperties die Properties, die addiert werden sollen
     */
    final public void addProperties(Properties otherProperties) {
        properties.addProperties(otherProperties);
        refreshAbilities();
    }

    /**
     * Zieht alle Eigenschaften der angegebenen Properties von den Eigenschaften dieses Chars ab.
     *
     * @param otherProperties die Properties, die subtrahiert werden sollen
     */
    final public void removeProperties(Properties otherProperties) {
        properties.removeProperties(otherProperties);
        refreshAbilities();
    }

    /**
     * Gibt die Properties dieses Chars zurück.
     *
     * @return die Propertie dieses Chars
     */
    protected Properties getProperties() {
        return properties;
    }
}
