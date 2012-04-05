package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Bullet;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.abilities.HitscanAbility;

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
        if (character.getProperty("hitpoints") < 0) {
            Server.game.netIDMap.remove(character.netID);
            Server.entityMap.removeEntity(character);
            DropManager.dropItem(character.getX(), character.getY(), 2);
        }
        Server.game.netIDMap.remove(bullet.netID);
        Server.entityMap.removeEntity(bullet);
    }

    public static void hitscanHit(Char character, HitscanAbility hitscanability) {
        hitscanability.applyEffectsToChar(character);
        if (character.getProperty("hitpoints") < 0) {
            Server.game.netIDMap.remove(character.netID);
            Server.entityMap.removeEntity(character);
            DropManager.dropItem(character.getX(), character.getY(), 2);
        }
    }

    /**
     * Berechnet Kollision eines Bullets mit einer Wand.
     */
    public static void bulletWallCollision() {
    }
}
