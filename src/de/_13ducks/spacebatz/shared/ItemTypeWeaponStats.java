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

/**
 * Waffenstats von Grundtypen
 * @author Jojo
 */
public class ItemTypeWeaponStats {
    private int damage;
    private double attackcooldown;
    private double range;
    private int weaponpic;
    private int bulletpic;
    private double bulletspeed;
    private double spread;
    private double explosionradius;
    
    public ItemTypeWeaponStats (int damage, double attackcooldown, double range, int weaponpic, int bulletpic, double bulletspeed, double spread, double explosionrange) {
        this.damage = damage;
        this.attackcooldown = attackcooldown;
        this.range = range;
        this.weaponpic = weaponpic;
        this.bulletpic = bulletpic;
        this.bulletspeed = bulletspeed;
        this.spread = spread;
        this.explosionradius = explosionrange;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the attackcooldown
     */
    public double getAttackcooldown() {
        return attackcooldown;
    }

    /**
     * @return the range
     */
    public double getRange() {
        return range;
    }

    /**
     * @return the bulletpic
     */
    public int getBulletpic() {
        return bulletpic;
    }

    /**
     * @return the bulletspeed
     */
    public double getBulletspeed() {
        return bulletspeed;
    }

    /**
     * @return the spread
     */
    public double getSpread() {
        return spread;
    }

    /**
     * @return the explosionrange
     */
    public double getExplosionradius() {
        return explosionradius;
    }

    /**
     * @return the weaponpic
     */
    public int getWeaponpic() {
        return weaponpic;
    }
}
