package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.gamelogic.HitManager;

/**
 * Explosionsschaden-Effekt
 *
 * @author michael
 */
public class ExplosionDamageEffect extends Effect {

    /**
     * Der angerichtetete Schaden
     */
    private int damage;
    /**
     * Der Umkreis, in dem Schaden gemacht wird
     */
    private double radius;
    
    public ExplosionDamageEffect(int damage, double radius) {
        this.damage = damage;
        this.radius = radius;
    }

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
        hitChar.decrementProperty("hitpoints", damage);
        HitManager.computeBulletExplosionCollision(damage, x, y, hitChar, radius);
    }

    @Override
    public void remove() {
        // da der Effekt nicht auf Chars angewandt wird wird er auch nicht entfernt
    }
}
