package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Klassen, die mit dem AstarPathfinder Wege berechnen wollen, müssen dieses Interface implementieren.
 * Dadurch erhalten sie die antwort auf ihre Anfrage.
 *
 * @author michael
 */
public interface PathRequester {

    /**
     * Wird aufgerufen, wenn ein angeforderter Pfad fertig ist.
     *
     * @param path der fertig berechnete Pfad
     */
    public void pathComputed(Vector path[]);
}
