package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Movement;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Repräsentiert den Wissensstand eines Clients
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientContext {

    private HashMap<Char, Movement> receivedMap;
    private HashMap<Movement, Char> sentMap;
    private HashSet<Integer> charMap;

    public ClientContext() {
        sentMap = new HashMap<>();
        receivedMap = new HashMap<>();
        charMap = new HashSet<>();
    }

    /**
     * Aufrufen, wenn ein Bewegungszustand zum Client geschickt wurde. Speichert diese Bewegung als versendet, überschreibt die möglicherweise zuletzt
     * gesendete.
     *
     * @param c der Char
     * @param m die Bewegung
     */
    public void sentMovement(Char c, Movement m) {
        if (c == null || m == null) {
            throw new IllegalArgumentException(c + " " + m);
        }
        sentMap.put(m, c);
    }

    /**
     * Testet, ob der Client von der gegebenen Bewegung der gegebenen Einheit schon weiß.
     *
     * @param c die Betroffene Einheit.
     * @param m die Bewegung, um die es geht.
     * @return true, wenn bekannt
     */
    public boolean knowsMovement(Char c, Movement m) {
        if (c == null || m == null) {
            throw new IllegalArgumentException(c + " " + m);
        }
        return m.equals(receivedMap.get(c));
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
     * Prüft, ob dieser Client von der Existenz dieses Chars weiß.
     *
     * @param c der Char, der getestet wird.
     * @return true, wenn bekannt, sonst false.
     */
    public boolean knowsChar(Char c) {
        return charMap.contains(c.getNetID());
    }

    /**
     * Setzt einen Char als bekannt.
     * In Zukunft wird knowsChar also true liefern.
     * @param c der ab sofort bekannte Char.
     */
    public void makeCharKnown(int netID) {
        if (!charMap.contains(netID)) {
            charMap.add(netID);
        }
    }
}