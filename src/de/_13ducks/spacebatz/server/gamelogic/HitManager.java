package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.Iterator;

/**
 * Berechnet alles, was passiert wenn zwei Dinge kollidieren (zb Bullets mit Chars).
 * Kümmert sich zB um das löschen von Bullets, Schaden, Effekte, etc...
 *
 * @author michael
 */
public class HitManager {

    /**
     * Berechnet Kollisionen zwischen Bullets und Chars.
     *
     * @param character der Char der mit dem Bullet kollidiert
     * @param bullet das Bullet das mit dem Char kollidiert
     */
    public static void charBulletCollision(Char character, Bullet bullet) {
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
     */
    public static void bulletWallCollision() {
    }

    /**
     * Berechnet Flächenschaden von Bullet-Explosionen
     * Bekommt den Char übergeben, den es direkt getroffen hat, damit dieser nicht nochmal Schaden kriegt
     */
    public static void computeBulletExplosionCollision(int damage, double x, double y, Char charhit, double radius) {
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
                            c.decrementProperty("hitpoints", damage * (1.0 - distance / radius * 0.66));
                        }
                    }
                }
            }
        }
    }
}