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

        ItemAttribute testarmor1 = new ItemAttribute("3");
        testarmor1.setPic(9);
        testarmor1.setItemClass(3);
        testarmor1.setQuality(1);
        testarmor1.setAmount(0);
        testarmor1.bonusStats.setArmor(4);
        testarmor1.bonusStats.setMovespeedMultiplicatorBonus(-0.15f);
        itemtypelist.add(testarmor1);

        ItemAttribute testarmor2 = new ItemAttribute("4");
        testarmor2.setPic(8);
        testarmor2.setItemClass(4);
        testarmor2.setQuality(1);
        testarmor2.setAmount(0);
        testarmor2.bonusStats.setMovespeedMultiplicatorBonus(0.15f);
        itemtypelist.add(testarmor2);

        ItemAttribute testarmor3 = new ItemAttribute("5");
        testarmor3.setPic(0);
        testarmor3.setItemClass(5);
        testarmor3.setQuality(1);
        testarmor3.setAmount(0);
        testarmor3.bonusStats.setArmor(1);
        itemtypelist.add(testarmor3);

        ItemAttribute hat = new ItemAttribute("Hat");
        hat.setPic(1);
        hat.setItemClass(2);
        hat.setQuality(1);
        hat.setAmount(0);
        hat.bonusStats.setArmor(2);
        itemtypelist.add(hat);

        ItemAttribute betterhat = new ItemAttribute("Better Hat");
        betterhat.setPic(1);
        betterhat.setItemClass(2);
        betterhat.setQuality(3);
        betterhat.setAmount(0);
        betterhat.bonusStats.setArmor(3);
        itemtypelist.add(betterhat);

        ItemAttribute pistol = new ItemAttribute("Pistol");
        pistol.setPic(4);
        pistol.setItemClass(1);
        pistol.setQuality(1);
        pistol.setAmount(0);
        pistol.getWeaponStats().setAttackOffset(1.0);
        pistol.setWeaponAbility(new FireBulletAbility(6, 4, 0.07, 15.0, 1, 0.45, 0.05, 0.0, 0, 0));
        itemtypelist.add(pistol);

        ItemAttribute shotgun = new ItemAttribute("Shotgun");
        shotgun.setPic(20);
        shotgun.setItemClass(1);
        shotgun.setQuality(1);
        shotgun.setAmount(0);
        shotgun.getWeaponStats().setAttackOffset(1.0);
        shotgun.setWeaponAbility(new FireMultipleBulletAbility(6, 2, 0.04, 11.0, 3, 0.45, 5, 0.1, 0.0, 8, 0.02));
        itemtypelist.add(shotgun);

        ItemAttribute shotgun2 = new ItemAttribute("Shotgun 2");
        shotgun2.setPic(21);
        shotgun2.setItemClass(1);
        shotgun2.setQuality(1);
        shotgun2.setAmount(0);
        shotgun2.getWeaponStats().setAttackOffset(1.0);
        shotgun2.setWeaponAbility(new FireMultipleBulletAbility(12, 2, 0.025, 16.0, 3, 0.5, 3, 0.09, 0.0, 6, 0.03));
        itemtypelist.add(shotgun2);

        ItemAttribute laser = new ItemAttribute("Laser");
        laser.setPic(2);
        laser.setItemClass(1);
        laser.setQuality(1);
        laser.setAmount(0);
        laser.getWeaponStats().setAttackOffset(1.0);
        laser.setWeaponAbility(new FireBulletAbility(5, 3, 0.16, 15.0, 3, 0.5, 0.06, 0.0, 32, 0.1));
        itemtypelist.add(laser);

        ItemAttribute superlaser = new ItemAttribute("Super Laser");
        superlaser.setPic(3);
        superlaser.setItemClass(1);
        superlaser.setQuality(1);
        superlaser.setAmount(0);
        superlaser.getWeaponStats().setAttackOffset(1.2);
        superlaser.setWeaponAbility(new FireBulletAbility(25, 5, 0.04, 20.0, 2, 0.75, 0.02, 0.0, 5, 0.015));
        itemtypelist.add(superlaser);

        ItemAttribute rocketlauncher = new ItemAttribute("Rocket Launcher");
        rocketlauncher.setPic(5);
        rocketlauncher.setItemClass(1);
        rocketlauncher.setQuality(1);
        rocketlauncher.setAmount(0);
        rocketlauncher.getWeaponStats().setAttackOffset(1.0);
        rocketlauncher.setWeaponAbility(new FireBulletAbility(20, 0, 0.025, 20.0, 4, 0.35, 0.0, 6.0, 4, 0.01));
        itemtypelist.add(rocketlauncher);

        ItemAttribute saw = new ItemAttribute("Saw");
        saw.setPic(7);
        saw.setItemClass(1);
        saw.setQuality(1);
        saw.setAmount(0);
        saw.setWeaponAbility(new HitscanAbility(20, 4, 0.08, 4.0, 10, 0.04));
        itemtypelist.add(saw);

        ItemAttribute superrocketlauncher = new ItemAttribute("Super Rocket Launcher");
        superrocketlauncher.setPic(17);
        superrocketlauncher.setItemClass(1);
        superrocketlauncher.setQuality(3);
        superrocketlauncher.setAmount(0);
        superrocketlauncher.getWeaponStats().setAttackOffset(1.0);
        superrocketlauncher.setWeaponAbility(new FireBulletAbility(12, 0, 0.08, 20.0, 16, 0.45, 0.0, 4.0, 12, 0.025));
        itemtypelist.add(superrocketlauncher);

        ItemAttribute superpistol = new ItemAttribute("Super Pistol");
        superpistol.setPic(16);
        superpistol.setItemClass(1);
        superpistol.setQuality(3);
        superpistol.setAmount(0);
        superpistol.getWeaponStats().setAttackOffset(1.0);
        superpistol.setWeaponAbility(new FireBulletAbility(12, 10, 0.1, 16.0, 1, 0.45, 0.02, 0.0, 0, 0));
        itemtypelist.add(superpistol);

        ItemAttribute drill = new ItemAttribute("Drill");
        drill.setPic(6);
        drill.setItemClass(6);
        drill.setQuality(1);
        drill.setAmount(0);
        drill.setWeaponAbility(new HitscanAbility(10, 5, 0.1, 4.0, 0, 0));
        itemtypelist.add(drill);
        
        ItemAttribute superdrill = new ItemAttribute("Super Drill");
        superdrill.setPic(18);
        superdrill.setItemClass(6);
        superdrill.setQuality(3);
        superdrill.setAmount(0);
        superdrill.setWeaponAbility(new HitscanAbility(20, 10, 0.12, 4.0, 0, 0));
        itemtypelist.add(superdrill);

    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
