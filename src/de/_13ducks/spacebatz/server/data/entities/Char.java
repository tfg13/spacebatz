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
package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.shared.PropertyList;

/**
 * Chars sind Entities, die eine dynamische Liste von Eigenschaften wie Hitpoints, Rüstung etc haben.
 *
 * @author michael
 */
public abstract class Char extends Entity {

    /**
     * Die Eigenschaften des Chars (Hitpoints, Rüstung etc).
     */
    private PropertyList properties;
    public int attackCooldownTick;

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
        properties = new PropertyList();
        properties.setHitpoints(Settings.CHARHEALTH);
        properties.setSightrange(10.0);
    }

    /**
     * Addiert alle Eigenschaften der angegebenen Properties zu den Properties des Chars.
     *
     * @param otherProperties die Properties, die addiert werden sollen
     */
    final public void addProperties(PropertyList otherProperties) {
        properties.addProperties(otherProperties);
        setSpeed(Settings.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Spped muss manuel gesetzt werden
    }

    /**
     * Zieht alle Eigenschaften der angegebenen Properties von den Eigenschaften dieses Chars ab.
     *
     * @param otherProperties die Properties, die subtrahiert werden sollen
     */
    final public void removeProperties(PropertyList otherProperties) {
        properties.removeProperties(otherProperties);
        setSpeed(Settings.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Spped muss manuel gesetzt werden
    }

    /**
     * Gibt die Properties dieses Chars zurück.
     *
     * @return die Propertie dieses Chars
     */
    public PropertyList getProperties() {
        return properties;
    }
}
