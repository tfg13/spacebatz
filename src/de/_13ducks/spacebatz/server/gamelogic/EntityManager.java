package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Verwaltet alle Enities eines Spiels. Synchronisiert Hinzufügen und Entfernen von Entities.
 *
 * @author michael
 */
public class EntityManager {

    /**
     * Alle dynamischen Objekte
     */
    private ConcurrentHashMap<Integer, Entity> netIDMap;
    private Stack<Integer> entitiesToRemove;

    public EntityManager() {
        netIDMap = new ConcurrentHashMap<>();
        entitiesToRemove = new Stack<>();
    }

    /**
     * Fügt eine neue Entity hinzu.
     *
     * Gibt ihr eine netID und synchronisiert sie mit den Clients.
     *
     * @param entity
     */
    public void addEntity(int netID, Entity entity) {
        netIDMap.put(netID, entity);
        Server.entityMap.insertEntity(entity);
    }

    /**
     * Entfernt alle Entities, die tot sind.
     */
    public void removeDisposableEntities() {
        while (!entitiesToRemove.empty()) {
            int netId = entitiesToRemove.pop();
            Server.entityMap.removeEntity(netIDMap.get(netId));
            Server.sync.entityRemoved(netIDMap.remove(netId));
        }
    }

    /**
     * Gibt einen Iterator über alle Entities zurück.
     *
     * @return
     */
    public Iterator<Entity> getEntityIterator() {
        return netIDMap.values().iterator();
    }

    public int getEntityCount() {
        return netIDMap.size();
    }

    public void removeEntity(int netID) {
        entitiesToRemove.add(netID);
    }

    public boolean containsEntity(int netID) {
        return netIDMap.containsKey(netID);
    }

    public Collection<Entity> getValues() {
        return netIDMap.values();
    }

    public Entity getEntityById(int netID) {
        return netIDMap.get(netID);
    }
}
