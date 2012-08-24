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
        moreDamage.itemStats.setBaseProperty("quality", 1.0);
        moreDamage.weaponStats.setMutliplicatorModifier("damage", 0.1);
        attributelist.add(moreDamage);

        ItemAttribute evenMoreDamage = new ItemAttribute("Even More Damage");
        evenMoreDamage.itemStats.setBaseProperty("quality", 2.0);
        evenMoreDamage.weaponStats.setMutliplicatorModifier("damage", 0.3);
        attributelist.add(evenMoreDamage);

        ItemAttribute moreAttackSpeed = new ItemAttribute("More Attackspeed");
        moreAttackSpeed.itemStats.setBaseProperty("quality", 1.0);
        moreAttackSpeed.weaponStats.setMutliplicatorModifier("attackspeed", 0.1);
        attributelist.add(moreAttackSpeed);

        ItemAttribute evenMoreAttackSpeed = new ItemAttribute("Even More Attackspeed");
        evenMoreAttackSpeed.itemStats.setBaseProperty("quality", 2.0);
        evenMoreAttackSpeed.weaponStats.setMutliplicatorModifier("attackspeed", 0.3);
        attributelist.add(evenMoreAttackSpeed);

        ItemAttribute moreRange = new ItemAttribute("More Range");
        moreRange.itemStats.setBaseProperty("quality", 2.0);
        moreRange.weaponStats.setMutliplicatorModifier("range", 0.2);
        attributelist.add(moreRange);

        ItemAttribute moreEverything = new ItemAttribute("More Everything");
        moreEverything.itemStats.setBaseProperty("quality", 3.0);
        moreEverything.weaponStats.setMutliplicatorModifier("damage", 0.1);
        moreEverything.weaponStats.setMutliplicatorModifier("attackspeed", 0.1);
        moreEverything.weaponStats.setMutliplicatorModifier("range", 0.1);
        attributelist.add(moreEverything);

        ItemAttribute moreArmor = new ItemAttribute("More Armor");
        moreArmor.itemStats.setBaseProperty("quality", 1.0);
        moreArmor.weaponStats.setMutliplicatorModifier("armor", 0.1);
        attributelist.add(moreArmor);

        ItemAttribute evenMoreArmor = new ItemAttribute("Even More Armor");
        evenMoreArmor.itemStats.setBaseProperty("quality", 2.0);
        evenMoreArmor.weaponStats.setMutliplicatorModifier("armor", 0.3);
        attributelist.add(evenMoreArmor);

        ItemAttribute moreHP = new ItemAttribute("More Healthpoints");
        moreHP.itemStats.setBaseProperty("quality", 1.0);
        moreHP.bonusStats.setBaseProperty("healthpoints", 5.0);
        attributelist.add(moreHP);

        ItemAttribute moreMovespeed = new ItemAttribute("More Movespeed");
        moreMovespeed.itemStats.setBaseProperty("quality", 2.0);
        moreMovespeed.bonusStats.setMutliplicatorModifier("movespeed", 0.2);
        attributelist.add(moreMovespeed);

        ItemAttribute unusual = new ItemAttribute("Unusual");
        unusual.itemStats.setBaseProperty("quality", 2.0);
        attributelist.add(unusual);
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
