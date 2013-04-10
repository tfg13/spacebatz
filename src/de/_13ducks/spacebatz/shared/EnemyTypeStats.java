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
 * Beschreibt alle eigenschaften eines Gegnertyps.
 *
 * @author Jj
 */
public class EnemyTypeStats implements java.io.Serializable {

    private static long serialVersionUID = 1L;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }
    private int healthpoints;
    private int damage;
    private int picture;
    private float speed;
    private int sightrange;
    private int enemylevel;
    private float color_red;
    private float color_green;
    private float color_blue;
    private float color_alpha;
    /**
     * Das Verhalten dieses Gegnertyps.
     */
    private BehaviourType behaviour;
    /**
     * Die Schieß-Fähigkeit dieses Gegnertyps.
     */
    private AbilityType shootAbility;

    /**
     * Liste der möglichen Gegner-Verhalten
     */
    public static enum BehaviourType {

        SHOOTER, SPECTATOR, KAMIKAZE
    };

    /**
     * Liste der möglichen Gegnerfähigkeiten.
     */
    public static enum AbilityType {

        FIREBULLET, NONE, KAMIKAZE
    };

    /**
     * @return the behaviour
     */
    public BehaviourType getBehaviour() {
        return behaviour;
    }

    /**
     * @param behaviour the behaviour to set
     */
    public void setBehaviour(BehaviourType behaviour) {
        this.behaviour = behaviour;
    }

    /**
     * @param healthpoints the healthpoints to set
     */
    public void setHealthpoints(int healthpoints) {
        this.healthpoints = healthpoints;
    }

    /**
     * @param damage the damage to set
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * @param picture the picture to set
     */
    public void setPicture(int picture) {
        this.picture = picture;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @param sightrange the sightrange to set
     */
    public void setSightrange(int sightrange) {
        this.sightrange = sightrange;
    }

    /**
     * @param enemylevel the enemylevel to set
     */
    public void setEnemylevel(int enemylevel) {
        this.enemylevel = enemylevel;
    }

    /**
     * @param color_red the color_red to set
     */
    public void setColor_red(float color_red) {
        this.color_red = color_red;
    }

    /**
     * @param color_green the color_green to set
     */
    public void setColor_green(float color_green) {
        this.color_green = color_green;
    }

    /**
     * @param color_blue the color_blue to set
     */
    public void setColor_blue(float color_blue) {
        this.color_blue = color_blue;
    }

    /**
     * @param color_alpha the color_alpha to set
     */
    public void setColor_alpha(float color_alpha) {
        this.color_alpha = color_alpha;
    }

    /**
     * @return the shootAbility
     */
    public AbilityType getShootAbility() {
        return shootAbility;
    }

    /**
     * @param shootAbility the shootAbility to set
     */
    public void setShootAbility(AbilityType shootAbility) {
        this.shootAbility = shootAbility;
    }

    /**
     * Initialisiert ein leeres EnemytypeStats-Objekt.
     */
    public EnemyTypeStats() {
        color_alpha = 1.0f;
        color_blue = 1.0f;
        color_green = 1.0f;
        color_red = 1.0f;
    }

    /**
     * Werte für einen Gegnertyp
     *
     * @param healthpoints Lebenspunkte
     * @param damage Schaden
     * @param picture Bildnummer in der Gegnertilemap
     * @param speed Geschwindigkeit
     * @param sightrange Distanz, ab der er auf en Spieler reagiert
     * @param enemylevel Gegnerlevel, soll gedroppte Items beeinflussen
     */
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
        this.enemylevel = enemylevel;
        this.color_red = 1f;
        this.color_blue = 1f;
        this.color_green = 1f;
        this.color_alpha = 1f;
    }

    /**
     * Werte für einen Gegnertyp mit Einfärben
     *
     * @param healthpoints Lebenspunkte
     * @param damage Schaden
     * @param picture Bildnummer in der Gegnertilemap
     * @param speed Geschwindigkeit
     * @param sightrange Distanz, ab der er auf en Spieler reagiert
     * @param enemylevel Gegnerlevel, soll gedroppte Items beeinflussen
     * @param color_red rote Farbe
     * @param color_blue blaue Farbe
     * @param color_green grüne Farbe
     * @param color_alpha Alpha-Farbwert
     */
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel, float color_red, float color_green, float color_blue, float color_alpha) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
        this.enemylevel = enemylevel;
        this.color_red = color_red;
        this.color_blue = color_blue;
        this.color_green = color_green;
        this.color_alpha = color_alpha;
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

    /**
     * @return the color_red
     */
    public float getColor_red() {
        return color_red;
    }

    /**
     * @return the color_green
     */
    public float getColor_green() {
        return color_green;
    }

    /**
     * @return the color_blue
     */
    public float getColor_blue() {
        return color_blue;
    }

    /**
     * @return the color_alpha
     */
    public float getColor_alpha() {
        return color_alpha;
    }
}
