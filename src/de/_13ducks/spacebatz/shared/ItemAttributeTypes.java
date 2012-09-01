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
        moreDamage.itemStats.setQuality(1);
        moreDamage.weaponStats.setDamageMultiplicatorBonus(0.1);
        attributelist.add(moreDamage);

        ItemAttribute evenMoreDamage = new ItemAttribute("Even More Damage");
        evenMoreDamage.itemStats.setQuality(2);
        evenMoreDamage.weaponStats.setDamageMultiplicatorBonus(0.3);
        attributelist.add(evenMoreDamage);

        ItemAttribute moreAttackSpeed = new ItemAttribute("More Attackspeed");
        moreAttackSpeed.itemStats.setQuality(1);
        moreAttackSpeed.weaponStats.setAttackSpeedMultiplicatorBonus(0.1);
        attributelist.add(moreAttackSpeed);

        ItemAttribute evenMoreAttackSpeed = new ItemAttribute("Even More Attackspeed");
        evenMoreAttackSpeed.itemStats.setQuality(2);
        evenMoreAttackSpeed.weaponStats.setAttackSpeedMultiplicatorBonus(0.3);
        attributelist.add(evenMoreAttackSpeed);

        ItemAttribute moreRange = new ItemAttribute("More Range");
        moreRange.itemStats.setQuality(2);
        moreRange.weaponStats.setRangeMultiplicatorBonus(0.2);
        attributelist.add(moreRange);

        ItemAttribute moreEverything = new ItemAttribute("More Everything");
        moreEverything.itemStats.setQuality(3);
        moreEverything.weaponStats.setDamageMultiplicatorBonus(0.1);
        moreEverything.weaponStats.setAttackSpeedMultiplicatorBonus(0.1);
        moreEverything.weaponStats.setRangeMultiplicatorBonus(0.1);
        attributelist.add(moreEverything);

        ItemAttribute moreArmor = new ItemAttribute("More Armor");
        moreArmor.itemStats.setQuality(1);
        moreArmor.weaponStats.setArmorMultiplicatorBonus(0.1);
        attributelist.add(moreArmor);

        ItemAttribute evenMoreArmor = new ItemAttribute("Even More Armor");
        evenMoreArmor.itemStats.setQuality(2);
        evenMoreArmor.weaponStats.setArmorMultiplicatorBonus(0.3);
        attributelist.add(evenMoreArmor);


//        ItemAttribute moreHP = new ItemAttribute("More Healthpoints");
//        moreHP.itemStats.setQuality(1);
//        moreHP.bonusStats.setHitpoints(5);
//        attributelist.add(moreHP);

        ItemAttribute moreMovespeed = new ItemAttribute("More Movespeed");
        moreMovespeed.itemStats.setQuality(2);
        moreMovespeed.bonusStats.setMovespeedMultiplicatorBonus(0.2);
        attributelist.add(moreMovespeed);

        ItemAttribute unusual = new ItemAttribute("Unusual");
        unusual.itemStats.setQuality(2);
        attributelist.add(unusual);
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
