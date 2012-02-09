package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Entity;

/**
 * Verwaltet die EntityMap, sorgt dafür das die Einheitenpositionen regelmäßig überprüft werden
 *
 * @author michael
 */
public class EntityMapManager {

    private int cooldown;

    /**
     * Berechnet provisorisch die Entitypositionen alle 20 frames neu
     */
    public void calculateEntityPositions() {
        cooldown--;
        if (cooldown < 0) {
            cooldown = 20;
            // Bullets:
            for (Entity e : Server.game.bullets) {
                Server.entityMap.entityMoved(e);
            }
            //Chars:
            for (Entity e : Server.game.netIDMap.values()) {
                Server.entityMap.entityMoved(e);
            }
        }


    }
}
