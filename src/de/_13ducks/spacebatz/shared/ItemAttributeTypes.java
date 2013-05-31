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

            ItemAttribute mostAtackspeed = new ItemAttribute("+50% Attackspeed");
            mostAtackspeed.setQuality(3);
            mostAtackspeed.weaponStats.setAttackspeedMultiplicatorBonus(0.50);
            mostAtackspeed.setIsWeaponAttribute(true);
            attributelist.add(mostAtackspeed);

            double r_evenMoreHpReg = randomValue(0.6, 1.0, 1);
            ItemAttribute moreHpReg = new ItemAttribute("+" + r_evenMoreHpReg + " HP Regeneration");
            moreHpReg.setQuality(3);
            moreHpReg.bonusStats.setHitpointRegeneration(r_evenMoreHpReg);
            attributelist.add(moreHpReg);

            int r_evenMorehp = (int) randomValue(31, 40, 0);
            ItemAttribute moreHP = new ItemAttribute("+" + r_evenMorehp + " Healthpoints");
            moreHP.setQuality(3);
            moreHP.bonusStats.setMaxHitpoints(r_evenMorehp);
            attributelist.add(moreHP);
        }
        if (quality >= 2) {

            double r_evenmoredamage = randomValue(0.4, 0.5, 1);
            ItemAttribute evenMoreDamage = new ItemAttribute("+" + (r_evenmoredamage * 100) + "% Damage");
            evenMoreDamage.setQuality(2);
            evenMoreDamage.weaponStats.setDamagemultiplicator(r_evenmoredamage);
            evenMoreDamage.setIsWeaponAttribute(true);
            attributelist.add(evenMoreDamage);

            double r_alldamage = randomValue(0.2, 0.3, 2);
            ItemAttribute allDamage = new ItemAttribute("+" + (r_alldamage * 100) + "% Damage For Every Weapon");
            allDamage.setQuality(2);
            allDamage.bonusStats.setDamageMultiplicatorBonus(r_alldamage);
            attributelist.add(allDamage);

            double r_evenmoreattackspeed = randomValue(0.25, 0.49, 2);
            ItemAttribute evenMoreAttackSpeed = new ItemAttribute("+" + (r_evenmoreattackspeed * 100) + "% Attackspeed");
            evenMoreAttackSpeed.setQuality(2);
            evenMoreAttackSpeed.weaponStats.setAttackspeedMultiplicatorBonus(r_evenmoreattackspeed);
            evenMoreAttackSpeed.setIsWeaponAttribute(true);
            attributelist.add(evenMoreAttackSpeed);

            double r_morearmor = randomValue(0.1, 0.2, 1);
            ItemAttribute evenMoreArmor = new ItemAttribute("+" + (r_morearmor * 100) + " % More Armor");
            evenMoreArmor.setQuality(2);
            evenMoreArmor.bonusStats.setArmorMultiplicatorBonus(r_morearmor);
            attributelist.add(evenMoreArmor);

            double r_moremovespeed = randomValue(0.05, 0.15, 2);
            ItemAttribute moreMovespeed = new ItemAttribute("+" + (r_moremovespeed * 100) + "% Movespeed");
            moreMovespeed.setQuality(2);
            moreMovespeed.bonusStats.setMovespeedMultiplicatorBonus(r_moremovespeed);
            attributelist.add(moreMovespeed);
        }
        if (quality >= 1) {

            double r_moredamage = randomValue(0.2, 0.3, 1);
            ItemAttribute moreDamage = new ItemAttribute("+" + (r_moredamage * 100) + "% Damage");
            moreDamage.setQuality(1);
            moreDamage.weaponStats.setDamagemultiplicator(r_moredamage);
            moreDamage.setIsWeaponAttribute(true);
            attributelist.add(moreDamage);

            double r_moreattackspeed = randomValue(0.1, 0.24, 2);
            ItemAttribute moreAttackSpeed = new ItemAttribute("+" + (r_moreattackspeed * 100) + "% Attackspeed");
            moreAttackSpeed.setQuality(1);
            moreAttackSpeed.weaponStats.setAttackspeedMultiplicatorBonus(r_moreattackspeed);
            moreAttackSpeed.setIsWeaponAttribute(true);
            attributelist.add(moreAttackSpeed);

            double r_morerange = randomValue(1, 4, 0);
            ItemAttribute moreRange = new ItemAttribute("+" + r_morerange + " Range");
            moreRange.setQuality(1);
            moreRange.weaponStats.setRange(r_morerange);
            moreRange.setIsWeaponAttribute(true);
            attributelist.add(moreRange);

            double r_moremaxoverheat = randomValue(0.3, 0.7, 1);
            ItemAttribute moreMaxOverheat = new ItemAttribute("+" + (r_moremaxoverheat * 100) + "% MaxOverheat");
            moreMaxOverheat.setQuality(1);
            moreMaxOverheat.weaponStats.setMaxoverheatMultiplicatorBonus(r_moremaxoverheat);
            moreMaxOverheat.setIsWeaponAttribute(true);
            attributelist.add(moreMaxOverheat);

            ItemAttribute moreReduceOverheat = new ItemAttribute("Faster Weapon Cooldown");
            moreReduceOverheat.setQuality(1);
            moreReduceOverheat.weaponStats.setReduceoverheat(0.02);
            moreReduceOverheat.setIsWeaponAttribute(true);
            attributelist.add(moreReduceOverheat);

            int r_morearmor = (int) randomValue(5, 10, 0);
            ItemAttribute moreArmor = new ItemAttribute("+" + r_morearmor + " Armor");
            moreArmor.setQuality(1);
            moreArmor.bonusStats.setArmor(r_morearmor);
            attributelist.add(moreArmor);

            int r_morehp = (int) randomValue(10, 30, 0);
            ItemAttribute moreHP = new ItemAttribute("+" + r_morehp + " Healthpoints");
            moreHP.setQuality(1);
            moreHP.bonusStats.setMaxHitpoints(r_morehp);
            attributelist.add(moreHP);

            double r_moreHpReg = randomValue(0.2, 0.5, 1);
            ItemAttribute moreHpReg = new ItemAttribute("+" + r_moreHpReg + " HP Regeneration");
            moreHpReg.setQuality(1);
            moreHpReg.bonusStats.setHitpointRegeneration(r_moreHpReg);
            attributelist.add(moreHpReg);
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
