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
    private double damage;
    private double attackspeed;
    private double range;
    private int bulletpic;
    private double bulletspeed;
    private double spread;
    private double explosionRadius;
    // PictureID setzen:
    private int pictureId;
    private double size;
    private double sightrange;
    /**
     * Der Bonus auf den Geschwindigkeitsmultiplikator. Der Geschwindigkeitsmultiplikator ist also 1.0 +
     * movespeedMultiplicatorBonus.
     */
    private double movespeedMultiplicatorBonus;
    private double attackcooldown;
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
    private int pic;
    private int itemclass;
    private int quality;
    private int amount;

    public PropertyList() {
    }

    /**
     * Addiert Attribute zu diesen hinzu.
     *
     * @param other die Attribute die addiert werden.
     */
    public void addProperties(PropertyList other) {
        hitpoints += other.getHitpoints();
        damage += other.getDamage();
        attackspeed += other.getAttackspeed();
        range += other.getRange();
        bulletpic += other.getBulletpic();
        bulletspeed += other.getBulletspeed();
        spread += other.getSpread();
        explosionRadius += other.getExplosionRadius();
        pictureId += other.getPictureId();
        size += other.getSize();
        sightrange += other.getSightrange();
        movespeedMultiplicatorBonus += other.getMovespeedMultiplicatorBonus();
        attackcooldown += other.getAttackcooldown();
        armorMultiplicatorBonus += other.getArmorMultiplicatorBonus();
        rangeMultiplicatorBonus += other.getRangeMultiplicatorBonus();
        damageMultiplicatorBonus += other.getDamageMultiplicatorBonus();
        attackSpeedMultiplicatorBonus += other.getAttackSpeedMultiplicatorBonus();
        pic += other.getPic();
        itemclass += other.getItemclass();
        quality += other.getQuality();
        amount += other.getAmount();
    }

    /**
     * Subtrahiert Attribute von diesen.
     *
     * @param other die Attribute die abgezogen werden.
     */
    public void removeProperties(PropertyList other) {
        hitpoints -= other.getHitpoints();
        damage -= other.getDamage();
        attackspeed -= other.getAttackspeed();
        range -= other.getRange();
        bulletpic -= other.getBulletpic();
        bulletspeed -= other.getBulletspeed();
        spread -= other.getSpread();
        explosionRadius -= other.getExplosionRadius();
        pictureId -= other.getPictureId();
        size -= other.getSize();
        sightrange -= other.getSightrange();
        movespeedMultiplicatorBonus -= other.getMovespeedMultiplicatorBonus();
        attackcooldown -= other.getAttackcooldown();
        armorMultiplicatorBonus -= other.getArmorMultiplicatorBonus();
        rangeMultiplicatorBonus -= other.getRangeMultiplicatorBonus();
        damageMultiplicatorBonus -= other.getDamageMultiplicatorBonus();
        attackSpeedMultiplicatorBonus -= other.getAttackSpeedMultiplicatorBonus();
        pic -= other.getPic();
        itemclass -= other.getItemclass();
        quality -= other.getQuality();
        amount -= other.getAmount();
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
     * @return the damage
     */
    public double getDamage() {
        return damage;
    }

    /**
     * @return the attackspeed
     */
    public double getAttackspeed() {
        return attackspeed;
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
     * @return the explosionRadius
     */
    public double getExplosionRadius() {
        return explosionRadius;
    }

    /**
     * @param damage the damage to set
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * @param attackspeed the attackspeed to set
     */
    public void setAttackspeed(double attackspeed) {
        this.attackspeed = attackspeed;
    }

    /**
     * @param range the range to set
     */
    public void setRange(double range) {
        this.range = range;
    }

    /**
     * @param bulletpic the bulletpic to set
     */
    public void setBulletpic(int bulletpic) {
        this.bulletpic = bulletpic;
    }

    /**
     * @param bulletspeed the bulletspeed to set
     */
    public void setBulletspeed(double bulletspeed) {
        this.bulletspeed = bulletspeed;
    }

    /**
     * @param spread the spread to set
     */
    public void setSpread(double spread) {
        this.spread = spread;
    }

    /**
     * @param explosionRadius the explosionRadius to set
     */
    public void setExplosionRadius(double explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    /**
     * @return the pictureId
     */
    public int getPictureId() {
        return pictureId;
    }

    /**
     * @param pictureId the pictureId to set
     */
    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(double size) {
        this.size = size;
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
     * @return the attackcooldown
     */
    public double getAttackcooldown() {
        return attackcooldown;
    }

    /**
     * @param attackcooldown the attackcooldown to set
     */
    public void setAttackcooldown(double attackcooldown) {
        this.attackcooldown = attackcooldown;
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
     * @return the itemclass
     */
    public int getItemclass() {
        return itemclass;
    }

    /**
     * @param itemclass the itemclass to set
     */
    public void setItemclass(int itemclass) {
        this.itemclass = itemclass;
    }

    /**
     * @return the quality
     */
    public int getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
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
     * @return the pic
     */
    public int getPic() {
        return pic;
    }

    /**
     * @param pic the pic to set
     */
    public void setPic(int pic) {
        this.pic = pic;
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
