/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.EffectCarrier;

/**
 *
 * @author michael
 */
public class PoisonEffect extends Effect {

    public PoisonEffect() {
        remainingTime = 100;
    }
    Char affectedChar;
    int remainingTime;

    @Override
    public boolean tick() {
        affectedChar.decrementProperty("hitpoints", 1);
        remainingTime--;
        if (remainingTime < 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void applyToChar(EffectCarrier affectedChar) {
        affectedChar.addTemporaryEffect(this);
        this.affectedChar = affectedChar;

    }

    @Override
    public void applyToPosition(double x, double y, EffectCarrier hitChar) {
    }

    @Override
    public void remove() {
    }
}
