package de._13ducks.spacebatz.shared;

import java.io.Serializable;

/**
 * Verwaltet Attribute von Chars oder Items.
 *
 * @author michael
 */
public class PropertyList implements Serializable {

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
    /**
     * Die Lebenspunkte
     */
    private int hitpoints;
    private double sightrange;
    /**
     * Der Bonus auf den Geschwindigkeitsmultiplikator. Der Geschwindigkeitsmultiplikator ist also 1.0 +
     * movespeedMultiplicatorBonus.
     */
    private double movespeedMultiplicatorBonus;
    /**
     * Der Bonus auf den Rüstungsmultiplikator. Der Rüstungsmultiplikator ist also 1.0 + armorMultiplicatorBonus.
     */
    private double armorMultiplicatorBonus;
    /**
     * Der Bonus auf den Reichweitemultiplikator. Der Reichweitemultiplikator ist also 1.0 + rangeMultiplicatorBonus.
     */
    private double rangeMultiplicatorBonus;
    /**
     * Der Bonus auf den Schadensmultiplikator. Der Schadensmultiplikator ist also 1.0 + damageMultiplicatorBonus.
     */
    private double damageMultiplicatorBonus;
    /**
     * Der Bonus auf den Angriffsgeschwindigkeitsmultiplikator. Der Angriffsgeschwindigkeitsmultiplikator ist also 1.0 +
     * attackSpeedMultiplicatorBonus.
     */
    private double attackSpeedMultiplicatorBonus;

    public PropertyList() {
    }

    /**
     * Addiert Attribute zu diesen hinzu.
     *
     * @param other die Attribute die addiert werden.
     */
    public void addProperties(PropertyList other) {
        hitpoints += other.getHitpoints();
        sightrange += other.getSightrange();
        movespeedMultiplicatorBonus += other.getMovespeedMultiplicatorBonus();
        armorMultiplicatorBonus += other.getArmorMultiplicatorBonus();
        rangeMultiplicatorBonus += other.getRangeMultiplicatorBonus();
        damageMultiplicatorBonus += other.getDamageMultiplicatorBonus();
        attackSpeedMultiplicatorBonus += other.getAttackSpeedMultiplicatorBonus();
    }

    /**
     * Subtrahiert Attribute von diesen.
     *
     * @param other die Attribute die abgezogen werden.
     */
    public void removeProperties(PropertyList other) {
        hitpoints -= other.getHitpoints();
        sightrange -= other.getSightrange();
        movespeedMultiplicatorBonus -= other.getMovespeedMultiplicatorBonus();
        armorMultiplicatorBonus -= other.getArmorMultiplicatorBonus();
        rangeMultiplicatorBonus -= other.getRangeMultiplicatorBonus();
        damageMultiplicatorBonus -= other.getDamageMultiplicatorBonus();
        attackSpeedMultiplicatorBonus -= other.getAttackSpeedMultiplicatorBonus();
    }

    /**
     * Gibt eine Textdarstellung dieser Attribute zurück.
     *
     * @return
     */
    @Override
    public String toString() {
        String text = "";
        text += "Hitpoints: " + getHitpoints();
        return text;
    }

    /**
     * @return the sightrange
     */
    public double getSightrange() {
        return sightrange;
    }

    /**
     * @param sightrange the sightrange to set
     */
    public void setSightrange(double sightrange) {
        this.sightrange = sightrange;
    }

    /**
     * @return the hitpoints
     */
    public int getHitpoints() {
        return hitpoints;
    }

    /**
     * @param hitpoints the hitpoints to set
     */
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    /**
     * @return the damageMultiplicatorBonus
     */
    public double getDamageMultiplicatorBonus() {
        return damageMultiplicatorBonus;
    }

    /**
     * @param damageMultiplicatorBonus the damageMultiplicatorBonus to set
     */
    public void setDamageMultiplicatorBonus(double damageMultiplicatorBonus) {
        this.damageMultiplicatorBonus = damageMultiplicatorBonus;
    }

    /**
     * @return the attackSpeedMultiplicatorBonus
     */
    public double getAttackSpeedMultiplicatorBonus() {
        return attackSpeedMultiplicatorBonus;
    }

    /**
     * @param attackSpeedMultiplicatorBonus the attackSpeedMultiplicatorBonus to set
     */
    public void setAttackSpeedMultiplicatorBonus(double attackSpeedMultiplicatorBonus) {
        this.attackSpeedMultiplicatorBonus = attackSpeedMultiplicatorBonus;
    }

    /**
     * @return the armorMultiplicatorBonus
     */
    public double getArmorMultiplicatorBonus() {
        return armorMultiplicatorBonus;
    }

    /**
     * @param armorMultiplicatorBonus the armorMultiplicatorBonus to set
     */
    public void setArmorMultiplicatorBonus(double armorMultiplicatorBonus) {
        this.armorMultiplicatorBonus = armorMultiplicatorBonus;
    }

    /**
     * @return the rangeMultiplicatorBonus
     */
    public double getRangeMultiplicatorBonus() {
        return rangeMultiplicatorBonus;
    }

    /**
     * @param rangeMultiplicatorBonus the rangeMultiplicatorBonus to set
     */
    public void setRangeMultiplicatorBonus(double rangeMultiplicatorBonus) {
        this.rangeMultiplicatorBonus = rangeMultiplicatorBonus;
    }

    /**
     * @return the movespeedMultiplicatorBonus
     */
    public double getMovespeedMultiplicatorBonus() {
        return movespeedMultiplicatorBonus;
    }

    /**
     * @param movespeedMultiplicatorBonus the movespeedMultiplicatorBonus to set
     */
    public void setMovespeedMultiplicatorBonus(double movespeedMultiplicatorBonus) {
        this.movespeedMultiplicatorBonus = movespeedMultiplicatorBonus;
    }
}
