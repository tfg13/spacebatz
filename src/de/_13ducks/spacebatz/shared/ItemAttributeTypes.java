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

        ItemAttribute moreDamage = new ItemAttribute("50% More Damage For This Weapon");
        moreDamage.setQuality(1);
        moreDamage.weaponStats.setDamagemultiplicator(0.5);
        moreDamage.setIsWeaponAttribute(true);
        attributelist.add(moreDamage);

        ItemAttribute evenMoreDamage = new ItemAttribute("50% More Damage For Every Weapon");
        evenMoreDamage.setQuality(2);
        evenMoreDamage.bonusStats.setDamageMultiplicatorBonus(0.5);
        attributelist.add(evenMoreDamage);

        ItemAttribute moreAttackSpeed = new ItemAttribute("3 More Attackspeed");
        moreAttackSpeed.setQuality(1);
        moreAttackSpeed.weaponStats.setAttackspeed(-3.0);
        moreAttackSpeed.setIsWeaponAttribute(true);
        attributelist.add(moreAttackSpeed);

        ItemAttribute evenMoreAttackSpeed = new ItemAttribute("5 More Attackspeed");
        evenMoreAttackSpeed.setQuality(2);
        evenMoreAttackSpeed.weaponStats.setAttackspeed(-5.0);
        evenMoreAttackSpeed.setIsWeaponAttribute(true);
        attributelist.add(evenMoreAttackSpeed);

        ItemAttribute moreRange = new ItemAttribute("3 More Range");
        moreRange.setQuality(1);
        moreRange.weaponStats.setRange(3);
        moreRange.setIsWeaponAttribute(true);
        attributelist.add(moreRange);

        ItemAttribute moreEverything = new ItemAttribute("More Everything");
        moreEverything.setQuality(3);
        moreEverything.bonusStats.setDamageMultiplicatorBonus(0.2);
        moreAttackSpeed.weaponStats.setAttackspeed(-2.0);
        moreEverything.weaponStats.setRange(1);
        moreEverything.setIsWeaponAttribute(true);
        attributelist.add(moreEverything);

//        ItemAttribute moreArmor = new ItemAttribute("0.1 More Armor");
//        moreArmor.setQuality(1);
//        moreArmor.bonusStats.setArmorMultiplicatorBonus(0.1);
//        attributelist.add(moreArmor);
//
//        ItemAttribute evenMoreArmor = new ItemAttribute("0.3 Even More Armor");
//        evenMoreArmor.setQuality(2);
//        evenMoreArmor.bonusStats.setArmorMultiplicatorBonus(0.3);
//        attributelist.add(evenMoreArmor);


//        ItemAttribute moreHP = new ItemAttribute("More Healthpoints");
//        moreHP.setQuality(1);
//        moreHP.bonusStats.setHitpoints(5);
//        attributelist.add(moreHP);

        ItemAttribute moreMovespeed = new ItemAttribute("0.2 More Movespeed");
        moreMovespeed.setQuality(2);
        moreMovespeed.bonusStats.setMovespeedMultiplicatorBonus(0.2);
        attributelist.add(moreMovespeed);

        ItemAttribute unusual = new ItemAttribute("Unusual");
        unusual.setQuality(2);
        attributelist.add(unusual);
        
        ItemAttribute moreMaxOverheat = new ItemAttribute("More MaxOverheat");
        moreMaxOverheat.setQuality(1);
        moreMaxOverheat.weaponStats.setMaxoverheat(10);
        moreMaxOverheat.setIsWeaponAttribute(true);
        attributelist.add(moreMaxOverheat);
        
        ItemAttribute moreReduceOverheat = new ItemAttribute("Faster Weapon Cooldown");
        moreReduceOverheat.setQuality(1);
        moreReduceOverheat.weaponStats.setReduceoverheat(0.01);
        moreReduceOverheat.setIsWeaponAttribute(true);
        attributelist.add(moreReduceOverheat);
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist() {
        return attributelist;
    }
}
