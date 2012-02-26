package de._13ducks.spacebatz.server.data;

/**
 * Für jede Waffe, die der Spieler angelegt hat, gibts ein Playerattack.
 * Wenn er mit so einer Waffe kämpft, werden dafür die Stats von hier benutzt.
 * @author jk
 */
public class PlayerAttack {

    /**
     * Schaden, den dieser Angriff macht
     */
    private int damage;
    /**
     * Cooldown-Zeit des Angriffs (in Ticks)
     */
    private double attackcooldown;
    /**
     * Reichweite des Angriffs (in Feldern)
     */
    private double range;
    /**
     * Bild-Nummer für Bullet
     */
    private int bulletpic;
    /**
     * Geschwindigkeit der Bullets
     */
    private double bulletspeed;
    /**
     * Streuung beim Schiessen
     */
    private double spread;
    /**
     * Explosionsradius des Bullets, meistens 0
     */
    private double explosionradius;

    /**
     * Konstruktor
     */
    public PlayerAttack(int damage, double attackcooldown, double range, int bulletpic, double bulletspeed, double spread, double explosionradius) {
        this.damage = damage;
        this.attackcooldown = attackcooldown;
        this.range = range;
        this.bulletpic = bulletpic;
        this.bulletspeed = bulletspeed;
        this.spread = spread;
        this.explosionradius = explosionradius;
    }

    public int getDamage() {
        return damage;
    }

    public double getAttackcooldown() {
        return attackcooldown;
    }

    public double getRange() {
        return range;
    }

    public double getBulletspeed() {
        return bulletspeed;
    }

    public double getSpread() {
        return spread;
    }

    public double getExplosionradius() {
        return explosionradius;
    }

    public int getBulletpic() {
        return bulletpic;
    }
}
