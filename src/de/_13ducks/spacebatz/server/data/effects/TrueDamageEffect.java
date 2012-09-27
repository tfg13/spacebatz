package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;

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
        affectedChar.getProperties().setHitpoints(affectedChar.getProperties().getHitpoints() - damage);
        STC_CHAR_HIT.sendCharHit(affectedChar.netID, damage, false);
    }

    @Override
    public void applyToPosition(double x, double y, Char hitChar) {
    }

    @Override
    public void remove() {
    }
}
