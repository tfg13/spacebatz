package de._13ducks.spacebatz.shared;

import java.io.Serializable;

/**
 * Stats einer Waffe.
 *
 * @author michael
 */
public class WeaponStats implements Serializable {

    /**
     * Der Schaden, den die Waffe anrichtet.
     */
    private double damage;
    /**
     * Um wieviel der zufällige Schaden vom Mittelwert abweichen darf
     */
    private double damagespread;
    /**
     * Der Schadensmultiplikator, wird auf den Schaden draufgerechnet.
     */
    private double damageMultiplicatorBonus;
    /**
     * Die Angriffsgeschwindigkeit.
     */
    private double attackspeed;
    /**
     * Der Angriffsgeschwindigkeit-Multiplikator.
     */
    private double attackspeedMultiplicatorBonus;
    /**
     * In welcher Distanz zum Spieler beim Schiessen z.B. das Bullet gespawnt wird
     */
    private double attackOffset;
    /**
     * Die Reichweite, die die Waffe hat.
     */
    private double range;
    /**
     * Das Bild der Geschosse.
     */
    private int bulletpic;
    /**
     * Die Geschwindigkeit der Geschosse.
     */
    private double bulletspeed;
    /**
     * Der Streufaktor der Geschosse.
     */
    private double spread;
    /**
     * Der Explosionsradius der Geschosse.
     */
    private double explosionRadius;
    // PictureID setzen:
    private int pictureId;
    /**
     * Nach wievielen Schüssen die Waffe überhitzt ist
     */
    private double maxoverheat;
    /**
     * Nach wievielen Schüssen die Waffe überhitzt ist
     */
    private double maxoverheatMultiplicatorBonus;
    /**
     * Wieviel Overheat pro Gametick abgebaut wird
     */
    private double reduceoverheat;
    /**
     * Bonus zu: Wieviel Overheat pro Gametick abgebaut wird
     */
    private double reduceoverheatMultiplicatorBonus;

    /**
     * Addiert Attribute zu diesen hinzu.
     *
     * @param other die Attribute die addiert werden.
     */
    public void addStats(WeaponStats other) {
        damage += other.getDamage();
        damagespread += other.getDamagespread();
        setDamageMultiplicatorBonus(damageMultiplicatorBonus + other.getDamageMultiplicatorBonus());
        attackspeed += other.getAttackspeed();
        setAttackspeedMultiplicatorBonus(getAttackspeedMultiplicatorBonus() + other.getAttackspeedMultiplicatorBonus());
        attackOffset += other.attackOffset;
        range += other.getRange();
        bulletpic += other.getBulletpic();
        bulletspeed += other.getBulletspeed();
        spread += other.getSpread();
        explosionRadius += other.getExplosionRadius();
        pictureId += other.getPictureId();
        // Maxoverheat = 0 bedeutet dass Waffe keinen Overheat hat. Wenn man addiert gehts kaputt
        if (maxoverheat > 0) {
            maxoverheat += other.getMaxoverheat();
        }
        setMaxoverheatMultiplicatorBonus(getMaxoverheatMultiplicatorBonus() + other.getMaxoverheatMultiplicatorBonus());
        reduceoverheat += other.getReduceoverheat();
        setReduceoverheatMultiplicatorBonus(getReduceoverheatMultiplicatorBonus() + other.getReduceoverheatMultiplicatorBonus());
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
     * @return the damagemultiplicator
     */
    public double getDamageMultiplicatorBonus() {
        return damageMultiplicatorBonus;
    }

    /**
     * @param damagemultiplicator the damagemultiplicator to set
     */
    public void setDamagemultiplicator(double damagemultiplicator) {
        this.setDamageMultiplicatorBonus(damagemultiplicator);
    }

    /**
     * @return the maxoverheat
     */
    public double getMaxoverheat() {
        return maxoverheat;
    }

    /**
     * @param maxoverheat the maxoverheat to set
     */
    public void setMaxoverheat(double maxoverheat) {
        this.maxoverheat = maxoverheat;
    }

    /**
     * @return the reduceoverheat
     */
    public double getReduceoverheat() {
        return reduceoverheat;
    }

    /**
     * @param reduceoverheat the reduceoverheat to set
     */
    public void setReduceoverheat(double reduceoverheat) {
        this.reduceoverheat = reduceoverheat;
    }

    /**
     * @return the damagespread
     */
    public double getDamagespread() {
        return damagespread;
    }

    /**
     * @param damagespread the damagespread to set
     */
    public void setDamagespread(double damagespread) {
        this.damagespread = damagespread;
    }

    public double getAttackOffset() {
        return attackOffset;
    }

    public void setAttackOffset(double attackOffset) {
        this.attackOffset = attackOffset;
    }

    /**
     * @param damageMultiplicatorBonus the damageMultiplicatorBonus to set
     */
    public void setDamageMultiplicatorBonus(double damageMultiplicatorBonus) {
        this.damageMultiplicatorBonus = damageMultiplicatorBonus;
    }

    /**
     * @return the attackspeedMultiplicatorBonus
     */
    public double getAttackspeedMultiplicatorBonus() {
        return attackspeedMultiplicatorBonus;
    }

    /**
     * @param attackspeedMultiplicatorBonus the attackspeedMultiplicatorBonus to set
     */
    public void setAttackspeedMultiplicatorBonus(double attackspeedMultiplicatorBonus) {
        this.attackspeedMultiplicatorBonus = attackspeedMultiplicatorBonus;
    }

    /**
     * @return the maxoverheatMultiplicatorBonus
     */
    public double getMaxoverheatMultiplicatorBonus() {
        return maxoverheatMultiplicatorBonus;
    }

    /**
     * @param maxoverheatMultiplicatorBonus the maxoverheatMultiplicatorBonus to set
     */
    public void setMaxoverheatMultiplicatorBonus(double maxoverheatMultiplicatorBonus) {
        this.maxoverheatMultiplicatorBonus = maxoverheatMultiplicatorBonus;
    }

    /**
     * @return the reduceoverheatMultiplicatorBonus
     */
    public double getReduceoverheatMultiplicatorBonus() {
        return reduceoverheatMultiplicatorBonus;
    }

    /**
     * @param reduceoverheatMultiplicatorBonus the reduceoverheatMultiplicatorBonus to set
     */
    public void setReduceoverheatMultiplicatorBonus(double reduceoverheatMultiplicatorBonus) {
        this.reduceoverheatMultiplicatorBonus = reduceoverheatMultiplicatorBonus;
    }
}
