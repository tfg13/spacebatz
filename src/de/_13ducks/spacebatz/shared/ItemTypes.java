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

import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.HitscanAbility;
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

//        ItemAttribute money = new ItemAttribute("Money");
//        money.setPic(0);
//        money.setItemClass(0);
//        money.setQuality(0); // nicht zufällig dropbar
//        money.setAmount(1);
//        itemtypelist.add(money);
//
//        ItemAttribute ore = new ItemAttribute("Iron Ore");
//        ore.setPic(8);
//        ore.setItemClass(0);
//        ore.setQuality(0); // nicht zufällig dropbar
//        ore.setAmount(1);
//        itemtypelist.add(ore);
//
//        ItemAttribute ore2 = new ItemAttribute("Gold Ore");
//        ore2.setPic(9);
//        ore2.setItemClass(0);
//        ore2.setQuality(0); // nicht zufällig dropbar
//        ore2.setAmount(1);
//        itemtypelist.add(ore2);

        ItemAttribute hat = new ItemAttribute("Hat");
        hat.setPic(1);
        hat.setItemClass(2);
        hat.setQuality(1);
        hat.setAmount(0);
        itemtypelist.add(hat);

        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.setPic(1);
        betterhat.setItemClass(2);
        betterhat.setQuality(3);
        betterhat.setAmount(0);
        itemtypelist.add(betterhat);

        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.setPic(4);
        pistol.setItemClass(1);
        pistol.setQuality(1);
        pistol.setAmount(0);
        pistol.setWeaponAbility(new FireBulletAbility(7, 6, 0.067, 12.0, 1, 0.35, 0.05, 0.0, 0, 0));
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setPic(2);
        laser.setItemClass(1);
        laser.setQuality(1);
        laser.setAmount(0);
        laser.setWeaponAbility(new FireBulletAbility(5, 3, 0.167, 11.0, 3, 0.35, 0.1, 0.0, 30, 0.05));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setPic(3);
        superlaser.setItemClass(1);
        superlaser.setQuality(1);
        superlaser.setAmount(0);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 20, 0.04, 15.0, 2, 0.5, 0.02, 0.0, 5, 0.01));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setPic(5);
        rocketlauncher.setItemClass(1);
        rocketlauncher.setQuality(1);
        rocketlauncher.setAmount(0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 0, 0.025, 17.0, 4, 0.25, 0.0, 6.0, 4, 0.01));
        itemtypelist.add(rocketlauncher);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.setPic(6);
        drill.setItemClass(1);
        drill.setQuality(1);
        drill.setAmount(0);
        drill.setWeaponAbility(new HitscanAbility(50, 5, 0.0167, 4.0, 0, 0));
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.setPic(7);
        saw.setItemClass(1);
        saw.setQuality(1);
        saw.setAmount(0);
        saw.setWeaponAbility(new HitscanAbility(15, 3, 0.083, 4.0, 10, 0.04));
        itemtypelist.add(saw);

        ItemAttribute superrocketlauncher = new ItemAttribute("Super Rocket Launcher");
        superrocketlauncher.setPic(17);
        superrocketlauncher.setItemClass(1);
        superrocketlauncher.setQuality(3);
        superrocketlauncher.setAmount(0);
        superrocketlauncher.setWeaponAbility(new FireBulletAbility(12, 0, 0.08, 17.0, 4, 0.35, 0.0, 4.0, 12, 0.025));
        itemtypelist.add(superrocketlauncher);

        ItemAttribute superpistol = new ItemAttribute("Super Pistol");
        superpistol.setPic(16);
        superpistol.setItemClass(1);
        superpistol.setQuality(3);
        superpistol.setAmount(0);
        superpistol.setWeaponAbility(new FireBulletAbility(12, 10, 0.1, 14.0, 1, 0.35, 0.02, 0.0, 0, 0));
        itemtypelist.add(superpistol);

        ItemAttribute superdrill = new ItemAttribute("Super Drill");
        superdrill.setPic(18);
        superdrill.setItemClass(1);
        superdrill.setQuality(3);
        superdrill.setAmount(0);
        superdrill.setWeaponAbility(new HitscanAbility(35, 20, 0.07, 4.0, 0, 0));
        itemtypelist.add(superdrill);


    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
