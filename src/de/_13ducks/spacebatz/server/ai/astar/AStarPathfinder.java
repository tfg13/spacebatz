package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.Position;

/**
 * Eine Implementierung des AStar Wegfindungsalgorithmus.
 *
 * Berechnet Wegfindungsanfragen und gibt das Ergebnis an den Anforderer zurück.
 *
 * @author michael
 */
public class AStarPathfinder {

    /**
     * Lässt den PAthfinder einen Weg berechnen. Diese Methode kehrt sofort zurück, der Weg wird im Hintergrund berechnet.
     * Wenn der Weg fertig berechnet ist wird er dem angegebenen requester geschickt.
     *
     * @param requestedPath der Weg der berechnet werden soll
     * @param requester der PathRequester, der das Ergebnis der berechnung erhält.
     */
    public void requestPath(Position start, Position target, PathRequester requester) {
    }
}
