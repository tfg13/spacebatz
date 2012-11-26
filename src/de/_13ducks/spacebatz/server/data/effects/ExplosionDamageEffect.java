package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;
import java.util.Iterator;

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
         * Hier wird der Flächenschaden berechnet und ausgeteilt. hitChar ist der Char der direkt getroffen wurde.
         */
        if (hitChar != null) {
            hitChar.getProperties().setHitpoints(hitChar.getProperties().getHitpoints() - damage);
            STC_CHAR_HIT.sendCharHit(hitChar.netID, damage, false);
        }
        computeBulletExplosion(damage, x, y, hitChar, radius);
    }

    @Override
    public void remove() {
        // da der Effekt nicht auf Chars angewandt wird wird er auch nicht entfernt
    }

    /**
     * Berechnet Flächenschaden von Bullet-Explosionen Bekommt den Char übergeben, den es direkt getroffen hat, damit
     * dieser nicht nochmal Schaden kriegt
     *
     * @param damage Schaden, den die Explosion macht (wird evtl durch Distanz abgeschwächt)
     * @param x Position
     * @param y Position
     * @param charhit Gegner, der direkt getroffen wird (oder null)
     * @param radius der Explosionsradius
     */
    public static void computeBulletExplosion(int damage, double x, double y, Char charhit, double radius) {
        Iterator<Entity> iter = Server.entityMap.getEntitiesAroundPoint(x, y, radius).iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char c = (Char) e;
                // Nicht für den direkt getroffenen durchführen
                if (!c.equals(charhit)) {
                    double distance = Math.sqrt((x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()));
                    // Zurzeit nur Gegnern Schaden machen
                    //if (c instanceof Enemy) {
                        int damagereduced = (int) (damage * (1.0 - distance / radius * 0.66)); // 34% - 100%
                        c.decreaseHitpoints(damagereduced);
                    //}
                }
            }
        }
    }
}
