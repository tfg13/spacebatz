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
        money.setPic(0);
        money.setItemClass(0);
        money.setQuality(0); // nicht zufällig dropbar
        money.setAmount(1);
        itemtypelist.add(money);

        ItemAttribute ore = new ItemAttribute("Iron Ore");
        ore.setPic(8);
        ore.setItemClass(0);
        ore.setQuality(0); // nicht zufällig dropbar
        ore.setAmount(1);
        itemtypelist.add(ore);

        ItemAttribute ore2 = new ItemAttribute("Gold Ore");
        ore2.setPic(9);
        ore2.setItemClass(0);
        ore2.setQuality(0); // nicht zufällig dropbar
        ore2.setAmount(1);
        itemtypelist.add(ore2);

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
        pistol.setWeaponAbility(new FireBulletAbility(7, 15.0, 10.0, 1, 0.35, 0.05, 0.0));
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setPic(2);
        laser.setItemClass(1);
        laser.setQuality(1);
        laser.setAmount(0);
        laser.setWeaponAbility(new FireBulletAbility(4, 6.0, 9.0, 3, 0.35, 0.1, 0.0));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setPic(3);
        superlaser.setItemClass(1);
        superlaser.setQuality(1);
        superlaser.setAmount(0);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 25.0, 12.0, 2, 0.5, 0.02, 0.0));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setPic(5);
        rocketlauncher.setItemClass(1);
        rocketlauncher.setQuality(1);
        rocketlauncher.setAmount(0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 40.0, 15.0, 4, 0.25, 0.0, 6.0));
        itemtypelist.add(rocketlauncher);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.setPic(6);
        drill.setItemClass(1);
        drill.setQuality(1);
        drill.setAmount(0);
        drill.setWeaponAbility(new HitscanAbility(50, 60.0, 4.0));
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.setPic(7);
        saw.setItemClass(1);
        saw.setQuality(1);
        saw.setAmount(0);
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
