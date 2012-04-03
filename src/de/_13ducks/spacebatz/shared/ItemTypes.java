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

import java.util.ArrayList;

/**
 * Liste aller Grundgegenstände (Geld, Waffen)
 *
 * @author j
 */
public class ItemTypes {

    private ArrayList<ItemAttribute> itemtypelist;

    public ItemTypes() {
        itemtypelist = new ArrayList<>();

        ItemAttribute money = new ItemAttribute("Money");
        money.setProperty("pic", 0);
        money.setProperty("itemclass", 0);
        money.setProperty("quality", 1);
        money.setProperty("amount", 1);
        itemtypelist.add(money);

        ItemAttribute hat = new ItemAttribute("Hat");
        hat.setProperty("pic", 1);
        hat.setProperty("itemclass", 2);
        hat.setProperty("quality", 1);
        hat.setProperty("amount", 0);
        itemtypelist.add(hat);

        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.setProperty("pic", 1);
        betterhat.setProperty("itemclass", 2);
        betterhat.setProperty("quality", 3);
        betterhat.setProperty("amount", 0);
        itemtypelist.add(betterhat);

        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.setProperty("pic", 4);
        pistol.setProperty("itemclass", 1);
        pistol.setProperty("quality", 1);
        pistol.setProperty("amount", 0);
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setProperty("pic", 2);
        laser.setProperty("itemclass", 1);
        laser.setProperty("quality", 1);
        laser.setProperty("amount", 0);
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setProperty("pic", 3);
        superlaser.setProperty("itemclass", 1);
        superlaser.setProperty("quality", 1);
        superlaser.setProperty("amount", 0);
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setProperty("pic", 5);
        rocketlauncher.setProperty("itemclass", 1);
        rocketlauncher.setProperty("quality", 1);
        rocketlauncher.setProperty("amount", 0);
        itemtypelist.add(rocketlauncher);



// name, pic, itemclass, quality, amount, armor, Waffenstats

//
//        itemtypelist.add(new ItemTypeStats("Money", 0, 0, 1, 1, 0, null));
//
//        itemtypelist.add(new ItemTypeStats("Hat", 1, 2, 1, 0, 23, null));
//
//        itemtypelist.add(new ItemTypeStats("Better Hat", 1, 2, 4, 0, 42, null));
//
//        ItemTypeWeaponStats pistol = new ItemTypeWeaponStats(5, 15.0, 10.0, 0, 1, 0.35, 0.025, 0.0);
//        itemtypelist.add(new ItemTypeStats("Pistol", 4, 1, 1, 0, 0, pistol));
//
//        ItemTypeWeaponStats laser = new ItemTypeWeaponStats(4, 6.0, 9.0, 0, 3, 0.35, 0.1, 0.0);
//        itemtypelist.add(new ItemTypeStats("Laser", 2, 1, 2, 0, 0, laser));
//
//        ItemTypeWeaponStats superlaser = new ItemTypeWeaponStats(25, 25.0, 12.0, 0, 2, 0.5, 0.02, 0.0);
//        itemtypelist.add(new ItemTypeStats("Super Laser", 3, 1, 3, 0, 0, superlaser));
//
//        ItemTypeWeaponStats rocketlauncher = new ItemTypeWeaponStats(20, 40.0, 15.0, 0, 4, 0.25, 0.0, 3.0);
//        itemtypelist.add(new ItemTypeStats("Rocket Launcher", 5, 1, 1, 0, 0, rocketlauncher));
    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
