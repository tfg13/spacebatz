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
 * Die zufälligen Attribute, die Items bekommen können
 *
 * @author Jojo
 */
public class ItemAttributeTypes {

    private ArrayList<ItemAttribute> attributelist;

    public ItemAttributeTypes() {
        attributelist = new ArrayList<>();
        // name, quality, damage, attackspeed, range, armor, movespeed


        ItemAttribute moreDamage = new ItemAttribute("More Damage");
        moreDamage.setWeaponProperty("quality", 1.0);
        moreDamage.setWeaponProperty("damagemultiplicator", 0.1);
        attributelist.add(moreDamage);

        ItemAttribute evenMoreDamage = new ItemAttribute("Even More Damage");
        evenMoreDamage.setWeaponProperty("quality", 2.0);
        evenMoreDamage.setWeaponProperty("damagemultiplicator", 0.3);
        attributelist.add(evenMoreDamage);

        ItemAttribute moreAttackSpeed = new ItemAttribute("More Attackspeed");
        moreAttackSpeed.setWeaponProperty("quality", 1.0);
        moreAttackSpeed.setWeaponProperty("attackspeedmultiplicator", 0.1);
        attributelist.add(moreAttackSpeed);

        ItemAttribute evenMoreAttackSpeed = new ItemAttribute("Even More Attackspeed");
        evenMoreAttackSpeed.setWeaponProperty("quality", 2.0);
        evenMoreAttackSpeed.setWeaponProperty("attackspeedmultiplicator", 0.3);
        attributelist.add(evenMoreAttackSpeed);

        ItemAttribute moreRange = new ItemAttribute("More Range");
        moreRange.setWeaponProperty("quality", 2.0);
        moreRange.setWeaponProperty("rangemultiplicator", 0.2);
        attributelist.add(moreRange);

        ItemAttribute moreEverything = new ItemAttribute("More Everything");
        moreEverything.setWeaponProperty("quality", 3.0);
        moreEverything.setWeaponProperty("damage", 0.1);
        moreEverything.setWeaponProperty("attackspeed", 0.1);
        moreEverything.setWeaponProperty("rangemultiplicator", 0.1);
        attributelist.add(moreEverything);

        ItemAttribute moreArmor = new ItemAttribute("More Armor");
        moreArmor.setWeaponProperty("quality", 1.0);
        moreArmor.setBonusProperty("armormultiplicator", 0.1);
        attributelist.add(moreArmor);

        ItemAttribute evenMoreArmor = new ItemAttribute("Even More Armor");
        evenMoreArmor.setWeaponProperty("quality", 2.0);
        evenMoreArmor.setBonusProperty("armormultiplicator", 0.3);
        attributelist.add(evenMoreArmor);

        ItemAttribute moreHP = new ItemAttribute("More Healthpoints");
        moreHP.setWeaponProperty("quality", 1.0);
        moreHP.setBonusProperty("healthpoints", 5.0);
        attributelist.add(moreHP);

        ItemAttribute moreMovespeed = new ItemAttribute("More Movespeed");
        moreMovespeed.setWeaponProperty("quality", 2.0);
        moreMovespeed.setBonusProperty("movespeedmultiplicator", 0.2);
        attributelist.add(moreMovespeed);

        ItemAttribute unusual = new ItemAttribute("Unusual");
        unusual.setWeaponProperty("quality", 2.0);
        attributelist.add(unusual);
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
