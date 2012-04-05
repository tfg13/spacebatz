package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Richtet Truedamage an, dh der Schaden wird ohne berücksichtigung von Rüstung angewandt.
 *
 * @author michael
 */
public class TrueDamageEffect extends Effect {

    /**
     * Erzeugt einen neuen TrueDamageEffect
     *
     * @param damage der Schaden, der angerichtet wird
     */
    public TrueDamageEffect(int damage) {
        this.damage = damage;
    }
    /**
     * Der angerichtetete Schaden
     */
    private int damage;

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public void applyToChar(Char affectedChar) {
        affectedChar.decrementProperty("hitpoints", damage);
    }

    @Override
    public void applyToPosition(double x, double y) {
    }

    @Override
    public void remove() {
    }
}
