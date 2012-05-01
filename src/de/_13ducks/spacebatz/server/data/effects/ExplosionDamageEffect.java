package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.Char;

/**
 * Explosionsschaden-Effekt
 *
 * @author michael
 */
public class ExplosionDamageEffect extends Effect {

    @Override
    public boolean tick() {
        // Dieser Effekt tickt nicht
        return false;
    }

    @Override
    public void applyToChar(Char affectedChar) {
        // Dieser Effekt wird nicht direkt auf Chars angewandt
    }

    @Override
    public void applyToPosition(double x, double y, Char hitChar) {
        /**
         * Hier wird der Flächenschaden berechnet und ausgeteilt.
         * hitChar ist der Char der direkt getroffen wurde.
         */
    }

    @Override
    public void remove() {
        // da der Effekt nicht auf Chars angewandt wird wird er auch nicht entfernt
    }
}
