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

/**
 * Items können Attribute besitzen, die jeweils einen oder mehrere Itemwerte verändern.
 *
 * @author Jojo
 */
public class ItemAttribute extends Properties {

    private static final long serialVersionUID = 1L;
    /**
     * Name des Attributs
     */
    private String name;

    /**
     * Erstellt ein neues Attribut und initialisiert es mit den angegebenen Properties.
     *
     * @param name der Name des Attributs
     * @param stats die initalisierungs-stats
     */
    public ItemAttribute(String name, Properties stats) {
        super();
        addProperties(stats);
        this.name = name;
    }

    /**
     * Erstellt eine neues Attribut ohne Werte.
     *
     * @param name der Name des Attributs
     */
    public ItemAttribute(String name) {
        super();
        this.name = name;
    }

    /**
     * Gibt den Namen dieses Attributs zurück.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
