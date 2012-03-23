package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Beschädigt den betroffenen Char
 *
 * @author michael
 */
public class DamageEffect extends CharEffect {

    /**
     * Der Schaden der angerichtet wird
     */
    private int damage;

    /**
     * Erzeugt einen neuen Schadenseffekt
     *
     * @param damage der Schaden der angerichtet werden soll
     */
    public DamageEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void onTick() {
        throw new IllegalStateException("Dieser Effekt muss nach der Anwendung verworfen werden!");
    }

    @Override
    public void applyToChar(Char affectedChar) {
        // affectedChar.damage()
    }

    @Override
    public void remove() {
        throw new IllegalStateException("Dieser Effekt muss nicht entfernt werden!");
    }
}
