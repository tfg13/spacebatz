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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.Item;

/**
 * Verwaltet Slots für die Items, die der Client (Client- und Serverseite) gerade angelegt hat
 * @author Jojo
 */
public class EquippedItems {
    /**
     * Enthält einzelne Slotarten, z.B. die Waffenslots, Armorslots
     */
    private Item[][] equipslots = new Item[3][];
    
    public EquippedItems() {
        Item[] wslot = new Item[3];
        Item[] aslot = new Item[1];
        
        equipslots[1] = wslot;
        equipslots[2] = aslot;
    }

    public Item[][] getEquipslots() {
        return equipslots;
    }
}
