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
        // Itemspezifische Eigenschaften:
        pistol.setItemProperty("pic", 4);
        pistol.setItemProperty("itemclass", 1);
        pistol.setItemProperty("quality", 1);
        pistol.setItemProperty("amount", 0);
        // Boni für Träger:
        pistol.setBonusProperty("canShoot", 1);
        pistol.setBonusProperty("shootDamage", 7);
        pistol.setBonusProperty("shootBulletSpeed", 0.35);
        pistol.setBonusProperty("shootRange", 10.0);
        pistol.setBonusProperty("shootSpread", 0.025);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(pistol);

        ItemAttribute laser = new ItemAttribute("laser");
        // Itemspezifische Eigenschaften:
        laser.setItemProperty("pic", 2);
        laser.setItemProperty("itemclass", 1);
        laser.setItemProperty("quality", 1);
        laser.setItemProperty("amount", 0);
        // Boni für Träger:
        laser.setBonusProperty("canShoot", 1);
        laser.setBonusProperty("shootDamage", 6);
        laser.setBonusProperty("shootBulletSpeed", 0.35);
        laser.setBonusProperty("shootRange", 9.0);
        laser.setBonusProperty("shootSpread", 0.1);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(laser);



        ItemAttribute superlaser = new ItemAttribute("superlaser");
        // Itemspezifische Eigenschaften:
        superlaser.setItemProperty("pic", 3);
        superlaser.setItemProperty("itemclass", 1);
        superlaser.setItemProperty("quality", 1);
        superlaser.setItemProperty("amount", 0);
        // Boni für Träger:
        superlaser.setBonusProperty("canShoot", 1);
        superlaser.setBonusProperty("shootDamage", 25);
        superlaser.setBonusProperty("shootBulletSpeed", 0.5);
        superlaser.setBonusProperty("shootRange", 12.0);
        superlaser.setBonusProperty("shootSpread", 0.02);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(superlaser);



        ItemAttribute rocketlauncher = new ItemAttribute("rocketlauncher");
        // Itemspezifische Eigenschaften:
        rocketlauncher.setItemProperty("pic", 5);
        rocketlauncher.setItemProperty("itemclass", 1);
        rocketlauncher.setItemProperty("quality", 1);
        rocketlauncher.setItemProperty("amount", 0);
        // Boni für Träger:
        rocketlauncher.setBonusProperty("canShoot", 1);
        rocketlauncher.setBonusProperty("shootDamage", 20);
        rocketlauncher.setBonusProperty("shootBulletSpeed", 0.25);
        rocketlauncher.setBonusProperty("shootRange", 15.0);
        rocketlauncher.setBonusProperty("shootSpread", 0.0);
        rocketlauncher.setBonusProperty("shootExplosionRadius", 3.0);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(rocketlauncher);


        ItemAttribute drill = new ItemAttribute("drill");
        // Itemspezifische Eigenschaften:
        drill.setItemProperty("pic", 6);
        drill.setItemProperty("itemclass", 1);
        drill.setItemProperty("quality", 1);
        drill.setItemProperty("amount", 0);
        // Boni für Träger:
        drill.setBonusProperty("canHitscan", 1);
        drill.setBonusProperty("hitscanDamage", 50);
        drill.setBonusProperty("hitscanRange", 4.0);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(drill);

        ItemAttribute saw = new ItemAttribute("saw");
        // Itemspezifische Eigenschaften:
        saw.setItemProperty("pic", 7);
        saw.setItemProperty("itemclass", 1);
        saw.setItemProperty("quality", 1);
        saw.setItemProperty("amount", 0);
        // Boni für Träger:
        saw.setBonusProperty("canHitscan", 1);
        saw.setBonusProperty("hitscanDamage", 15);
        saw.setBonusProperty("hitscanRange", 4.0);
        // TODO: COOLDOWN/ATTACKSPEED setzen
        itemtypelist.add(saw);


    }

    /**
     * @return the itemtypelist
     */
    public ArrayList<ItemAttribute> getItemtypelist() {
        return itemtypelist;
    }
}
