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
    private int attackcooldown;
    /**
     * Reichweite des Angriffs (in Feldern)
     */
    private double range;
    
    /**
     * Konstruktor
     */
    public PlayerAttack(int damage, int attackcooldown, double range) {
        this.damage = damage;
        this.attackcooldown = attackcooldown;
        this.range = range;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackcooldown() {
        return attackcooldown;
    }

    public double getRange() {
        return range;
    }
}
