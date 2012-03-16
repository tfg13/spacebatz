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
 * Die Werte, die eine bestimmte Gegnersortesorte hat
 * @author Jj
 */
public class EnemyTypeStats implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private int healthpoints;
    private int damage;
    private int picture;
    private float speed;
    private int sightrange;
    private int enemylevel;
    
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
        this.enemylevel = enemylevel;
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the picture
     */
    public int getPicture() {
        return picture;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the sightrange
     */
    public int getSightrange() {
        return sightrange;
    }

    /**
     * @return the enemylevel
     */
    public int getEnemylevel() {
        return enemylevel;
    }
}
