package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.EffectCarrier;
import de._13ducks.spacebatz.server.network.ServerMessageSender;

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
    public void applyToChar(EffectCarrier affectedChar) {
        affectedChar.decrementProperty("hitpoints", damage);
        Server.msgSender.sendCharHit(affectedChar.netID, damage, false);
    }

    @Override
    public void applyToPosition(double x, double y, EffectCarrier hitChar) {
    }

    @Override
    public void remove() {
    }
}
