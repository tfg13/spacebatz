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
    private Item[][] equipslots = new Item[7][];
    
    public EquippedItems() {
        Item[] weaponslot = new Item[3];
        Item[] armorslot2 = new Item[2];
        Item[] armorslot3 = new Item[1];
        Item[] armorslot4 = new Item[1];
        Item[] armorslot5 = new Item[1];
        Item[] toolslot = new Item[2];

        equipslots[1] = weaponslot;
        equipslots[2] = armorslot2;
        equipslots[3] = armorslot3;
        equipslots[4] = armorslot4;
        equipslots[5] = armorslot5;
        equipslots[6] = toolslot;
    }

    public Item[][] getEquipslots() {
        return equipslots;
    }
}
