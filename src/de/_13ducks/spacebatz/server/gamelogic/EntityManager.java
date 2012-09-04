package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author michael
 */
public class EntityManager {

    /**
     * Alle dynamischen Objekte
     */
    public ConcurrentHashMap<Integer, Entity> netIDMap;

    public EntityManager() {
        netIDMap = new ConcurrentHashMap<>();
    }
}
