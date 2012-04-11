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

    private ArrayList<ItemBaseAttribute> itemtypelist;

    public ItemTypes() {
        itemtypelist = new ArrayList<>();

        ItemBaseAttribute money = new ItemBaseAttribute("Money");
        money.setBonusProperty("pic", 0);
        money.setBonusProperty("itemclass", 0);
        money.setBonusProperty("quality", 1);
        money.setBonusProperty("amount", 1);
        itemtypelist.add(money);

        ItemBaseAttribute hat = new ItemBaseAttribute("Hat");
        hat.setBonusProperty("pic", 1);
        hat.setBonusProperty("itemclass", 2);
        hat.setBonusProperty("quality", 1);
        hat.setBonusProperty("amount", 0);
        itemtypelist.add(hat);

        ItemBaseAttribute betterhat = new ItemBaseAttribute("Better Hat");
        betterhat.setBonusProperty("pic", 1);
        betterhat.setBonusProperty("itemclass", 2);
        betterhat.setBonusProperty("quality", 3);
        betterhat.setBonusProperty("amount", 0);
        itemtypelist.add(betterhat);

        ItemBaseAttribute pistol = new ItemBaseAttribute("Pistol");
        pistol.setBonusProperty("pic", 4);
        pistol.setBonusProperty("itemclass", 1);
        pistol.setBonusProperty("quality", 1);
        pistol.setBonusProperty("amount", 0);
        pistol.setAbility(new FireBulletAbility(7, 15.0, 10.0, 1, 0.35, 0.025, 0.0));
        itemtypelist.add(pistol);

        ItemBaseAttribute laser = new ItemBaseAttribute("Laser");
        laser.setBonusProperty("pic", 2);
        laser.setBonusProperty("itemclass", 1);
        laser.setBonusProperty("quality", 1);
        laser.setBonusProperty("amount", 0);
        laser.setAbility(new FireBulletAbility(4, 6.0, 9.0, 3, 0.35, 0.1, 0.0));
        itemtypelist.add(laser);

        ItemBaseAttribute superlaser = new ItemBaseAttribute("Super Laser");
        superlaser.setBonusProperty("pic", 3);
        superlaser.setBonusProperty("itemclass", 1);
        superlaser.setBonusProperty("quality", 1);
        superlaser.setBonusProperty("amount", 0);
        superlaser.setAbility(new FireBulletAbility(25, 25.0, 12.0, 2, 0.5, 0.02, 0.0));
        itemtypelist.add(superlaser);

        ItemBaseAttribute rocketlauncher = new ItemBaseAttribute("Rocket Launcher");
        rocketlauncher.setBonusProperty("pic", 5);
        rocketlauncher.setBonusProperty("itemclass", 1);
        rocketlauncher.setBonusProperty("quality", 1);
        rocketlauncher.setBonusProperty("amount", 0);
        rocketlauncher.setAbility(new FireBulletAbility(20, 40.0, 15.0, 4, 0.25, 0.0, 3.0));
        itemtypelist.add(rocketlauncher);

        ItemBaseAttribute drill = new ItemBaseAttribute("Drill");
        drill.setBonusProperty("pic", 6);
        drill.setBonusProperty("itemclass", 1);
        drill.setBonusProperty("quality", 1);
        drill.setBonusProperty("amount", 0);
        drill.setAbility(new HitscanAbility(50, 60.0, 4.0));
        itemtypelist.add(drill);

        ItemBaseAttribute saw = new ItemBaseAttribute("Saw");
        saw.setBonusProperty("pic", 7);
        saw.setBonusProperty("itemclass", 1);
        saw.setBonusProperty("quality", 1);
        saw.setBonusProperty("amount", 0);
        saw.setAbility(new HitscanAbility(15, 12.0, 4.0));
        itemtypelist.add(saw);

    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemBaseAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
