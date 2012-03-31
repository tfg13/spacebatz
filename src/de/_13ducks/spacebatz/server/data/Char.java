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
import java.util.HashMap;

/**
 * Ein bewegliches Objekt, das eine dynamische Liste von Eigenschaften hat.
 * Eigenschaften können z.B. Hitpoints, Sichtweite, etc... sein.
 *
 * @author michael
 */
public abstract class Char extends Entity {

    /**
     * Die Liste der Eigenschaften des Chars, bestehend aus Name-Wert Paaren
     */
    private HashMap<String, Double> properties;

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
        properties = new HashMap<>();

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
        if (properties.containsKey(name)) {
            return properties.get(name);
        } else {
            return 0;
        }
    }

    /**
     * Inkrementiert den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der inkrementiert werden soll
     * @param value der Wert, um den die Eigenschaft inkrementiert werden soll
     */
    final public void incrementProperty(String name, double value) {
        if (properties.containsKey(name)) {
            properties.put(name, properties.get(name) + value);
        } else {
            properties.put(name, value);
        }
    }

    /**
     * Dekrementiert den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der dekrementiert werden soll
     * @param value der Wert, um den die Eigenschaft dekrementiert werden soll
     */
    final public void decrementProperty(String name, double value) {
        if (properties.containsKey(name)) {
            properties.put(name, properties.get(name) - value);
        } else {
            properties.put(name, -value);
        }
    }

    /**
     * Setzt den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der gesetzt werden soll
     * @param value der Wert, auf den die Eigenschaft gesetzt werden soll
     */
    final public void setProperty(String name, double value) {
        properties.put(name, value);
    }
}
