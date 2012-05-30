package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.Iterator;

/**
 * Berechnet Schaden.
 * Kümmert sich zB um das löschen von Bullets, Schaden, Effekte, etc...
 *
 * @author michael
 */
public class HitManager {

    /**
     * Berechnet Schaden, wenn Bullet etwas trifft.
     *
     * @param character der Char der Schaden nehmen soll
     * @param bullet das Bullet das Schaden austeilt
     */
    public static void charBulletHit(Char character, Bullet bullet) {
        // abbrechen, wenn der Char das Bullet selber erzeugt hat:
        if (bullet.getOwner().equals(character)) {
            return;
        }
        // alle Effekte des Bullets auf den Char übertragen:
        bullet.applyEffectsToChar(character);
        bullet.activateEffectsAtPosition(bullet.getX(), bullet.getY(), character);
        if (character.getProperty("hitpoints") <= 0) {
            Server.game.netIDMap.remove(character.netID);
            Server.entityMap.removeEntity(character);
            DropManager.dropItem(character.getX(), character.getY(), 2);
        }
        Server.game.netIDMap.remove(bullet.netID);
        Server.entityMap.removeEntity(bullet);
    }

    /**
     * Berechnet Kollision eines Bullets mit einer Wand.
     * Leider ist das hier nicht der Collisionmanager.
     */
    public static void bulletWallCollision() {
    }

    /**
     * Berechnet Flächenschaden von Bullet-Explosionen
     * Bekommt den Char übergeben, den es direkt getroffen hat, damit dieser nicht nochmal Schaden kriegt
     * 
     * @param damage Schaden, den die Explosion macht (wird evtl durch Distanz abgeschwächt)
     * @param x Position
     * @param y Position
     * @param charhit Gegner, der direkt getroffen wird (oder null)
     * @param radius der Explosionsradius
     */
    public static void computeBulletExplosion(int damage, double x, double y, Char charhit, double radius) {
        Iterator<Entity> iter = Server.game.netIDMap.values().iterator();
        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e instanceof Char) {
                Char c = (Char) e;
                // Nicht für den direkt getroffenen durchführen
                if (!c.equals(charhit)) {
                    double distance = Math.sqrt((x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()));
                    if (distance < radius) {
                        // Zurzeit nur Gegnern Schaden machen
                        if (c instanceof Enemy) {
                            int damagereduced = (int) (damage * (1.0 - distance / radius * 0.66)); // 34% - 100%
                            c.decrementProperty("hitpoints", damagereduced);
                            Server.msgSender.sendCharHit(c.netID, damagereduced, false);
                        }
                    }
                }
            }
        }
    }
}