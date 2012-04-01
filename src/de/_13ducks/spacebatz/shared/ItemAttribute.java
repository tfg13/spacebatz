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

    public ItemAttribute(String name, Properties stats) {
        super();
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
