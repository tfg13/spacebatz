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
    }

    /**
     * @return the attributelist
     */
    public ArrayList<ItemAttribute> getAttributelist(int quality) {
        attributelist = new ArrayList<>();
        if (quality >= 3) {

            ItemAttribute moreEverything = new ItemAttribute("More Everything");
            moreEverything.setQuality(3);
            moreEverything.weaponStats.setDamagemultiplicator(0.5);
            moreEverything.weaponStats.setAttackspeed(0.03);
            moreEverything.weaponStats.setRange(3);
            moreEverything.setIsWeaponAttribute(true);
            attributelist.add(moreEverything);
        }
        if (quality >= 2) {

            double r_evenmoredamage = randomValue(0.8, 0.9, 1);
            ItemAttribute evenMoreDamage = new ItemAttribute("*" + (r_evenmoredamage + 1) + " Damage");
            evenMoreDamage.setQuality(2);
            evenMoreDamage.weaponStats.setDamagemultiplicator(r_evenmoredamage);
            evenMoreDamage.setIsWeaponAttribute(true);
            attributelist.add(evenMoreDamage);
            
            double r_alldamage = randomValue(0.2, 0.5, 1);
            ItemAttribute allDamage = new ItemAttribute("*" + (r_alldamage + 1) + " Damage For Every Weapon");
            allDamage.setQuality(2);
            allDamage.bonusStats.setDamageMultiplicatorBonus(r_alldamage);
            attributelist.add(allDamage);

            double r_evenmoreattackspeed = randomValue(0.04, 0.06, 2);
            ItemAttribute evenMoreAttackSpeed = new ItemAttribute("*" + (r_evenmoreattackspeed + 1) + " Attackspeed");
            evenMoreAttackSpeed.setQuality(2);
            evenMoreAttackSpeed.weaponStats.setAttackspeed(r_evenmoreattackspeed);
            evenMoreAttackSpeed.setIsWeaponAttribute(true);
            attributelist.add(evenMoreAttackSpeed);

            //        ItemAttribute evenMoreArmor = new ItemAttribute("0.3 Even More Armor");
            //        evenMoreArmor.setQuality(2);
            //        evenMoreArmor.bonusStats.setArmorMultiplicatorBonus(0.3);
            //        attributelist.add(evenMoreArmor);

            double r_moremovespeed = randomValue(0.05, 0.20, 2);
            ItemAttribute moreMovespeed = new ItemAttribute("*" + (r_moremovespeed + 1) + " Movespeed");
            moreMovespeed.setQuality(2);
            moreMovespeed.bonusStats.setMovespeedMultiplicatorBonus(r_moremovespeed);
            attributelist.add(moreMovespeed);

            ItemAttribute unusual = new ItemAttribute("Unusual");
            unusual.setQuality(2);
            attributelist.add(unusual);
        }
        if (quality >= 1) {

            double r_moredamage = randomValue(0.2, 0.7, 1);
            ItemAttribute moreDamage = new ItemAttribute("*" + (r_moredamage + 1) + " Damage");
            moreDamage.setQuality(1);
            moreDamage.weaponStats.setDamagemultiplicator(r_moredamage);
            moreDamage.setIsWeaponAttribute(true);
            attributelist.add(moreDamage);

            double r_moreattackspeed = randomValue(0.02, 0.04, 2);
            ItemAttribute moreAttackSpeed = new ItemAttribute("*" + (r_moreattackspeed + 1) + " Attackspeed");
            moreAttackSpeed.setQuality(1);
            moreAttackSpeed.weaponStats.setAttackspeed(r_moreattackspeed);
            moreAttackSpeed.setIsWeaponAttribute(true);
            attributelist.add(moreAttackSpeed);

            double r_morerange = randomValue(1, 3, 0);
            ItemAttribute moreRange = new ItemAttribute("+" + r_morerange + " Range");
            moreRange.setQuality(1);
            moreRange.weaponStats.setRange(r_morerange);
            moreRange.setIsWeaponAttribute(true);
            attributelist.add(moreRange);

            double r_moremaxoverheat = randomValue(5, 12, 0);
            ItemAttribute moreMaxOverheat = new ItemAttribute("+" + r_moremaxoverheat + " MaxOverheat");
            moreMaxOverheat.setQuality(1);
            moreMaxOverheat.weaponStats.setMaxoverheat(r_moremaxoverheat);
            moreMaxOverheat.setIsWeaponAttribute(true);
            attributelist.add(moreMaxOverheat);

            ItemAttribute moreReduceOverheat = new ItemAttribute("*1.01 Faster Weapon Cooldown");
            moreReduceOverheat.setQuality(1);
            moreReduceOverheat.weaponStats.setReduceoverheat(0.01);
            moreReduceOverheat.setIsWeaponAttribute(true);
            attributelist.add(moreReduceOverheat);

            //        ItemAttribute moreArmor = new ItemAttribute("0.1 More Armor");
            //        moreArmor.setQuality(1);
            //        moreArmor.bonusStats.setArmorMultiplicatorBonus(0.1);
            //        attributelist.add(moreArmor);

            //        ItemAttribute moreHP = new ItemAttribute("More Healthpoints");
            //        moreHP.setQuality(1);
            //        moreHP.bonusStats.setHitpoints(20);
            //        attributelist.add(moreHP);
        }
        return attributelist;
    }

    /**
     * Gibt Zufallswert zwischen 2 Zahlen zurück, mit bestimmter Anzahl Stellen nach dem Komma
     *
     * @param d1 unterste mögliche Zahl
     * @param d2 unterste mögliche Zahl
     * @param stellen Anzahl Stellen nach Komma
     * @return Zufallszahl
     */
    private double randomValue(double d1, double d2, int stellen) {
        d2 += 1.0 / Math.pow(10, stellen);
        double wert = Math.random() * (d2 - d1) + d1;

        wert *= Math.pow(10, stellen);
        wert = (int) wert;
        wert /= Math.pow(10, stellen);

        return wert;
    }
}
