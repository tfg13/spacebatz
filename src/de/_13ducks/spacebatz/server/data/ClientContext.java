package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Movement;
import java.util.HashMap;

/**
 * Repräsentiert den Wissensstand eines Clients
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientContext {

    private HashMap<Char, Movement> moveMap;

    public ClientContext() {
        moveMap = new HashMap<>();
    }

    /**
     * Testet, ob der Client von der gegebenen Bewegung der gegebenen Einheit schon weiß.
     *
     * @param c die Betroffene Einheit.
     * @param m die Bewegung, um die es geht.
     * @return true, wenn bekannt
     */
    public boolean knows(Char c, Movement m) {
        if (c == null || m == null) {
            throw new IllegalArgumentException(c + " " + m);
        }
        return m.equals(moveMap.get(c));
    }

    /**
     * Setzt eine Bewegung als bekannt.
     *
     * @param c die Einheit
     * @param m die Bewegung
     */
    public void makeKnown(Char c, Movement m) {
        if (c == null || m == null) {
            throw new IllegalArgumentException(c + " " + m);
        }
        moveMap.put(c, m);
    }
}