package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.Char;

/**
 * Richtet normalen Damage an
 *
 * @author michael
 */
public class StandardDamageEffect extends Effect {

    /**
     * Erzeugt einen neuen StandardDamageEffect
     *
     * @param damage der Schaden, der angerichtet wird
     */
    public StandardDamageEffect(int damage) {
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
        affectedChar.decreaseHitpoints(damage);
    }

    @Override
    public void applyToPosition(double x, double y, Char hitChar) {
    }

    @Override
    public void remove() {
    }
}
