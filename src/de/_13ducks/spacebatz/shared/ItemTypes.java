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
        money.setItemProperty("pic", 0);
        money.setItemProperty("itemclass", 0);
        money.setItemProperty("quality", 1);
        money.setItemProperty("amount", 1);
        itemtypelist.add(money);

        ItemAttribute ore = new ItemAttribute("Iron Ore");
        ore.setItemProperty("pic", 8);
        ore.setItemProperty("itemclass", 0);
        ore.setItemProperty("quality", 0); // nicht zufällig dropbar
        ore.setItemProperty("amount", 1);
        itemtypelist.add(ore);

        ItemAttribute ore2 = new ItemAttribute("Gold Ore");
        ore2.setItemProperty("pic", 9);
        ore2.setItemProperty("itemclass", 0);
        ore2.setItemProperty("quality", 0); // nicht zufällig dropbar
        ore2.setItemProperty("amount", 1);
        itemtypelist.add(ore2);

        ItemAttribute hat = new ItemAttribute("Hat");
        hat.setItemProperty("pic", 1);
        hat.setItemProperty("itemclass", 2);
        hat.setItemProperty("quality", 1);
        hat.setItemProperty("amount", 0);
        itemtypelist.add(hat);

        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.setItemProperty("pic", 1);
        betterhat.setItemProperty("itemclass", 2);
        betterhat.setItemProperty("quality", 3);
        betterhat.setItemProperty("amount", 0);
        itemtypelist.add(betterhat);

        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.setItemProperty("pic", 4);
        pistol.setItemProperty("itemclass", 1);
        pistol.setItemProperty("quality", 1);
        pistol.setItemProperty("amount", 0);
        pistol.setWeaponAbility(new FireBulletAbility(7, 15.0, 10.0, 1, 0.35, 0.05, 0.0));
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setItemProperty("pic", 2);
        laser.setItemProperty("itemclass", 1);
        laser.setItemProperty("quality", 1);
        laser.setItemProperty("amount", 0);
        laser.setWeaponAbility(new FireBulletAbility(4, 6.0, 9.0, 3, 0.35, 0.1, 0.0));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setItemProperty("pic", 3);
        superlaser.setItemProperty("itemclass", 1);
        superlaser.setItemProperty("quality", 1);
        superlaser.setItemProperty("amount", 0);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 25.0, 12.0, 2, 0.5, 0.02, 0.0));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setItemProperty("pic", 5);
        rocketlauncher.setItemProperty("itemclass", 1);
        rocketlauncher.setItemProperty("quality", 1);
        rocketlauncher.setItemProperty("amount", 0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 40.0, 15.0, 4, 0.25, 0.0, 3.0));
        itemtypelist.add(rocketlauncher);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.setItemProperty("pic", 6);
        drill.setItemProperty("itemclass", 1);
        drill.setItemProperty("quality", 1);
        drill.setItemProperty("amount", 0);
        drill.setWeaponAbility(new HitscanAbility(50, 60.0, 4.0));
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.setItemProperty("pic", 7);
        saw.setItemProperty("itemclass", 1);
        saw.setItemProperty("quality", 1);
        saw.setItemProperty("amount", 0);
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
