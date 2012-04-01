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


        Properties moreDamage = new Properties();
        moreDamage.setProperty("quality", 1.0);
        moreDamage.setProperty("damage", 0.1);
        attributelist.add(new ItemAttribute("More Damage", moreDamage));

        Properties evenMoreDamage = new Properties();
        evenMoreDamage.setProperty("quality", 2.0);
        evenMoreDamage.setProperty("damage", 0.3);
        attributelist.add(new ItemAttribute("Even More Damage", evenMoreDamage));

        Properties moreAttackSpeed = new Properties();
        moreAttackSpeed.setProperty("quality", 1.0);
        moreAttackSpeed.setProperty("attackspeed", 0.1);
        attributelist.add(new ItemAttribute("More Attackspeed", moreAttackSpeed));

        Properties evenMoreAttackSpeed = new Properties();
        evenMoreAttackSpeed.setProperty("quality", 2.0);
        evenMoreAttackSpeed.setProperty("attackspeed", 0.3);
        attributelist.add(new ItemAttribute("Even More Attackspeed", evenMoreAttackSpeed));

        Properties moreRange = new Properties();
        moreRange.setProperty("quality", 2.0);
        moreRange.setProperty("range", 0.2);
        attributelist.add(new ItemAttribute("More Range", moreRange));

        Properties moreEverything = new Properties();
        moreEverything.setProperty("quality", 3.0);
        moreEverything.setProperty("damage", 0.1);
        moreEverything.setProperty("attackspeed", 0.1);
        moreEverything.setProperty("range", 0.1);
        attributelist.add(new ItemAttribute("More Everything", moreEverything));

        Properties moreArmor = new Properties();
        moreArmor.setProperty("quality", 1.0);
        moreArmor.setProperty("armor", 0.1);
        attributelist.add(new ItemAttribute("More Armor", moreArmor));

        Properties evenMoreArmor = new Properties();
        evenMoreArmor.setProperty("quality", 2.0);
        evenMoreArmor.setProperty("armor", 0.3);
        attributelist.add(new ItemAttribute("Even More Armor", evenMoreArmor));

        Properties moreHP = new Properties();
        moreHP.setProperty("quality", 1.0);
        moreHP.setProperty("healthpoints", 5.0);
        attributelist.add(new ItemAttribute("More Healthpoints", moreHP));

        Properties moreMovespeed = new Properties();
        moreMovespeed.setProperty("quality", 2.0);
        moreMovespeed.setProperty("movespeed", 0.2);
        attributelist.add(new ItemAttribute("More Movespeed", moreMovespeed));

        Properties unusual = new Properties();
        unusual.setProperty("quality", 2.0);
        attributelist.add(new ItemAttribute("Unusual", unusual));
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
