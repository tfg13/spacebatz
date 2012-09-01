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
        money.itemStats.setPic(0);
        money.itemStats.setItemclass(0);
        money.itemStats.setQuality(1);
        money.itemStats.setAmount(1);
        itemtypelist.add(money);
        
        ItemAttribute ore = new ItemAttribute("Iron Ore");
        ore.itemStats.setPic(8);
        ore.itemStats.setItemclass(0);
        ore.itemStats.setQuality(0); // nicht zufällig dropbar
        ore.itemStats.setAmount(1);
        itemtypelist.add(ore);
        
        ItemAttribute ore2 = new ItemAttribute("Gold Ore");
        ore2.itemStats.setPic(9);
        ore2.itemStats.setItemclass(0);
        ore2.itemStats.setQuality(0); // nicht zufällig dropbar
        ore2.itemStats.setAmount(1);
        itemtypelist.add(ore2);
        
        ItemAttribute hat = new ItemAttribute("Hat");
        hat.itemStats.setPic(1);
        hat.itemStats.setItemclass(2);
        hat.itemStats.setQuality(1);
        hat.itemStats.setAmount(0);
        itemtypelist.add(hat);
        
        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.itemStats.setPic(1);
        betterhat.itemStats.setItemclass(2);
        betterhat.itemStats.setQuality(3);
        betterhat.itemStats.setAmount(0);
        itemtypelist.add(betterhat);
        
        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.itemStats.setPic(4);
        pistol.itemStats.setItemclass(1);
        pistol.itemStats.setQuality(1);
        pistol.itemStats.setAmount(0);
        pistol.setWeaponAbility(new FireBulletAbility(7, 15.0, 10.0, 1, 0.35, 0.05, 0.0));
        itemtypelist.add(pistol);
        
        ItemAttribute laser = new ItemAttribute("Laser");
        laser.itemStats.setPic(2);
        laser.itemStats.setItemclass(1);
        laser.itemStats.setQuality(1);
        laser.itemStats.setAmount(0);
        laser.setWeaponAbility(new FireBulletAbility(4, 6.0, 9.0, 3, 0.35, 0.1, 0.0));
        itemtypelist.add(laser);
        
        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.itemStats.setPic(3);
        superlaser.itemStats.setItemclass(1);
        superlaser.itemStats.setQuality(1);
        superlaser.itemStats.setAmount(0);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 25.0, 12.0, 2, 0.5, 0.02, 0.0));
        itemtypelist.add(superlaser);
        
        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.itemStats.setPic(5);
        rocketlauncher.itemStats.setItemclass(1);
        rocketlauncher.itemStats.setQuality(1);
        rocketlauncher.itemStats.setAmount(0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 40.0, 15.0, 4, 0.25, 0.0, 6.0));
        itemtypelist.add(rocketlauncher);
        
        ItemAttribute drill = new ItemAttribute("Drill");
        drill.itemStats.setPic(6);
        drill.itemStats.setItemclass(1);
        drill.itemStats.setQuality(1);
        drill.itemStats.setAmount(0);
        drill.setWeaponAbility(new HitscanAbility(50, 60.0, 4.0));
        itemtypelist.add(drill);
        
        ItemAttribute saw = new ItemAttribute("Saw");
        saw.itemStats.setPic(7);
        saw.itemStats.setItemclass(1);
        saw.itemStats.setQuality(1);
        saw.itemStats.setAmount(0);
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
