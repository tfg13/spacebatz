package de._13ducks.spacebatz.shared;

import java.io.Serializable;

public class WeaponStats implements Serializable {

    private double damage;
    private double attackspeed;
    private double range;
    private int bulletpic;
    private double bulletspeed;
    private double spread;
    private double explosionRadius;
    // PictureID setzen:
    private int pictureId;

    /**
     * Addiert Attribute zu diesen hinzu.
     *
     * @param other die Attribute die addiert werden.
     */
    public void addStats(WeaponStats other) {
        damage += other.getDamage();
        attackspeed += other.getAttackspeed();
        range += other.getRange();
        bulletpic += other.getBulletpic();
        bulletspeed += other.getBulletspeed();
        spread += other.getSpread();
        explosionRadius += other.getExplosionRadius();
        pictureId += other.getPictureId();


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
}
