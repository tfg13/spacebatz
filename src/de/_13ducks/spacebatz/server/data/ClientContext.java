package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Movement;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Repräsentiert den Wissensstand eines Clients
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientContext {

    private HashMap<Entity, Movement> receivedMap;
    private HashMap<Movement, Entity> sentMap;
    private HashMap<Integer, Entity> entityMap;

    public ClientContext() {
        sentMap = new HashMap<>();
        receivedMap = new HashMap<>();
        entityMap = new HashMap<>();
    }

    /**
     * Aufrufen, wenn ein Bewegungszustand zum Client geschickt wurde. Speichert diese Bewegung als versendet, überschreibt die möglicherweise zuletzt
     * gesendete.
     *
     * @param e das Entity
     * @param m die Bewegung
     */
    public void sentMovement(Entity e, Movement m) {
        if (e == null || m == null) {
            throw new IllegalArgumentException(e + " " + m);
        }
        sentMap.put(m, e);
    }

    /**
     * Testet, ob der Client von der gegebenen Bewegung der gegebenen Einheit schon weiß.
     *
     * @param e die Betroffene Einheit.
     * @param m die Bewegung, um die es geht.
     * @return true, wenn bekannt
     */
    public boolean knowsMovement(Entity e, Movement m) {
        if (e == null || m == null) {
            throw new IllegalArgumentException(e + " " + m);
        }
        return m.equals(receivedMap.get(e));
    }

    /**
     * Setzt eine Bewegung als bekannt.
     *
     * @param movementHash der hashCode-Wert dieses Movements
     */
    public void makeMovementKnown(int movementHash) {
        for (Movement m : sentMap.keySet()) {
            if (m.hashCode() == movementHash) {
                // Gefunden!
                receivedMap.put(sentMap.get(m), m);
            }
        }
    }

    /**
     * Prüft, ob dieser Client von der Existenz dieses Entitys weiß.
     *
     * @param e das Entity, das getestet wird.
     * @return true, wenn bekannt, sonst false.
     */
    public boolean knowsEntity(Entity e) {
        return entityMap.containsKey(e.netID);
    }

    /**
     * Setzt ein Entity als bekannt.
     * In Zukunft wird knowsChar also true liefern.
     * @param c der ab sofort bekannte Char.
     */
    public void makeEntityKnown(int netID) {
        if (!entityMap.containsKey(netID)) {
            Sync e = Server.game.netIDMap.get(netID);
	    if (e instanceof Entity) {
		entityMap.put(netID, (Entity) e);
	    } else {
		System.out.println("ERROR: Client-Context kann derzeit nur Entitys verwalten!");
	    }
        }
    }

    /**
     * Liefert einen Iterator über alle dem Client bekannten Entitys.
     * @return einen Iterator über alle dem Client bekannten Entitys.
     */
    public Iterator<Entity> knownEntiysIterator() {
        return entityMap.values().iterator();
    }

    /**
     * Löscht ein Entity aus dem Kontext des Clients.
     * Dieser Client kennt das Entity zukünftig nicht mehr.
     * @param netID Die netID des zu löschenden Entitys.
     */
    public void removeEntity(int netID) {
        entityMap.remove(netID);
    }
}