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
import de._13ducks.spacebatz.server.data.abilities.FireMultipleBulletAbility;
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
        pistol.getWeaponStats().setAttackOffset(1.0);
        pistol.setWeaponAbility(new FireBulletAbility(7, 5, 0.067, 15.0, 1, 0.45, 0.05, 0.0, 0, 0));
        itemtypelist.add(pistol);

        ItemAttribute shotgun = new ItemAttribute("Shotgun");
        shotgun.setPic(20);
        shotgun.setItemClass(1);
        shotgun.setQuality(1);
        shotgun.setAmount(0);
        shotgun.getWeaponStats().setAttackOffset(1.0);
        shotgun.setWeaponAbility(new FireMultipleBulletAbility(7, 2, 0.04, 11.0, 3, 0.45, 5, 0.1, 0.0, 8, 0.02));
        itemtypelist.add(shotgun);

        ItemAttribute shotgun2 = new ItemAttribute("Shotgun 2");
        shotgun2.setPic(21);
        shotgun2.setItemClass(1);
        shotgun2.setQuality(1);
        shotgun2.setAmount(0);
        shotgun2.getWeaponStats().setAttackOffset(1.0);
        shotgun2.setWeaponAbility(new FireMultipleBulletAbility(13, 3, 0.025, 16.0, 3, 0.5, 3, 0.1, 0.0, 6, 0.03));
        itemtypelist.add(shotgun2);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setPic(2);
        laser.setItemClass(1);
        laser.setQuality(1);
        laser.setAmount(0);
        laser.getWeaponStats().setAttackOffset(1.0);
        laser.setWeaponAbility(new FireBulletAbility(5, 3, 0.16, 15.0, 3, 0.45, 0.07, 0.0, 32, 0.1));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setPic(3);
        superlaser.setItemClass(1);
        superlaser.setQuality(1);
        superlaser.setAmount(0);
        superlaser.getWeaponStats().setAttackOffset(1.2);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 10, 0.04, 20.0, 2, 0.75, 0.02, 0.0, 5, 0.01));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setPic(5);
        rocketlauncher.setItemClass(1);
        rocketlauncher.setQuality(1);
        rocketlauncher.setAmount(0);
        rocketlauncher.getWeaponStats().setAttackOffset(1.0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 0, 0.025, 20.0, 4, 0.35, 0.0, 6.0, 4, 0.01));
        itemtypelist.add(rocketlauncher);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.setPic(6);
        drill.setItemClass(1);
        drill.setQuality(1);
        drill.setAmount(0);
        drill.setWeaponAbility(new HitscanAbility(50, 5, 0.02, 4.0, 0, 0));
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.setPic(7);
        saw.setItemClass(1);
        saw.setQuality(1);
        saw.setAmount(0);
        saw.setWeaponAbility(new HitscanAbility(15, 3, 0.08, 4.0, 10, 0.04));
        itemtypelist.add(saw);

        ItemAttribute superrocketlauncher = new ItemAttribute("Super Rocket Launcher");
        superrocketlauncher.setPic(17);
        superrocketlauncher.setItemClass(1);
        superrocketlauncher.setQuality(3);
        superrocketlauncher.setAmount(0);
        superrocketlauncher.getWeaponStats().setAttackOffset(1.0);
        superrocketlauncher.setWeaponAbility(new FireBulletAbility(12, 0, 0.08, 20.0, 4, 0.45, 0.0, 4.0, 12, 0.025));
        itemtypelist.add(superrocketlauncher);

        ItemAttribute superpistol = new ItemAttribute("Super Pistol");
        superpistol.setPic(16);
        superpistol.setItemClass(1);
        superpistol.setQuality(3);
        superpistol.setAmount(0);
        superpistol.getWeaponStats().setAttackOffset(1.0);
        superpistol.setWeaponAbility(new FireBulletAbility(12, 10, 0.1, 16.0, 1, 0.45, 0.02, 0.0, 0, 0));
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
