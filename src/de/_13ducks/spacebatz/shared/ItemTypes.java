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

        ItemAttribute money = new ItemAttribute("Money");
        money.itemStats.setBaseProperty("pic", 0);
        money.itemStats.setBaseProperty("itemclass", 0);
        money.itemStats.setBaseProperty("quality", 1);
        money.itemStats.setBaseProperty("amount", 1);
        itemtypelist.add(money);

        ItemAttribute ore = new ItemAttribute("Iron Ore");
        ore.itemStats.setBaseProperty("pic", 8);
        ore.itemStats.setBaseProperty("itemclass", 0);
        ore.itemStats.setBaseProperty("quality", 0); // nicht zufällig dropbar
        ore.itemStats.setBaseProperty("amount", 1);
        itemtypelist.add(ore);

        ItemAttribute ore2 = new ItemAttribute("Gold Ore");
        ore2.itemStats.setBaseProperty("pic", 9);
        ore2.itemStats.setBaseProperty("itemclass", 0);
        ore2.itemStats.setBaseProperty("quality", 0); // nicht zufällig dropbar
        ore2.itemStats.setBaseProperty("amount", 1);
        itemtypelist.add(ore2);

        ItemAttribute hat = new ItemAttribute("Hat");
        hat.itemStats.setBaseProperty("pic", 1);
        hat.itemStats.setBaseProperty("itemclass", 2);
        hat.itemStats.setBaseProperty("quality", 1);
        hat.itemStats.setBaseProperty("amount", 0);
        itemtypelist.add(hat);

        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.itemStats.setBaseProperty("pic", 1);
        betterhat.itemStats.setBaseProperty("itemclass", 2);
        betterhat.itemStats.setBaseProperty("quality", 3);
        betterhat.itemStats.setBaseProperty("amount", 0);
        itemtypelist.add(betterhat);

        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.itemStats.setBaseProperty("pic", 4);
        pistol.itemStats.setBaseProperty("itemclass", 1);
        pistol.itemStats.setBaseProperty("quality", 1);
        pistol.itemStats.setBaseProperty("amount", 0);
        pistol.setWeaponAbility(new FireBulletAbility(7, 15.0, 10.0, 1, 0.35, 0.05, 0.0));
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.itemStats.setBaseProperty("pic", 2);
        laser.itemStats.setBaseProperty("itemclass", 1);
        laser.itemStats.setBaseProperty("quality", 1);
        laser.itemStats.setBaseProperty("amount", 0);
        laser.setWeaponAbility(new FireBulletAbility(4, 6.0, 9.0, 3, 0.35, 0.1, 0.0));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.itemStats.setBaseProperty("pic", 3);
        superlaser.itemStats.setBaseProperty("itemclass", 1);
        superlaser.itemStats.setBaseProperty("quality", 1);
        superlaser.itemStats.setBaseProperty("amount", 0);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 25.0, 12.0, 2, 0.5, 0.02, 0.0));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.itemStats.setBaseProperty("pic", 5);
        rocketlauncher.itemStats.setBaseProperty("itemclass", 1);
        rocketlauncher.itemStats.setBaseProperty("quality", 1);
        rocketlauncher.itemStats.setBaseProperty("amount", 0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 40.0, 15.0, 4, 0.25, 0.0, 6.0));
        itemtypelist.add(rocketlauncher);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.itemStats.setBaseProperty("pic", 6);
        drill.itemStats.setBaseProperty("itemclass", 1);
        drill.itemStats.setBaseProperty("quality", 1);
        drill.itemStats.setBaseProperty("amount", 0);
        drill.setWeaponAbility(new HitscanAbility(50, 60.0, 4.0));
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.itemStats.setBaseProperty("pic", 7);
        saw.itemStats.setBaseProperty("itemclass", 1);
        saw.itemStats.setBaseProperty("quality", 1);
        saw.itemStats.setBaseProperty("amount", 0);
        saw.setWeaponAbility(new HitscanAbility(15, 12.0, 4.0));
        itemtypelist.add(saw);


    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
