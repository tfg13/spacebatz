package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Position;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Eine Implementierung des AStar Wegfindungsalgorithmus.
 *
 * Berechnet Wegfindungsanfragen und gibt das Ergebnis an den Anforderer zurück.
 *
 * @author michael
 */
public class AStarPathfinder {

    /**
     * Die maximalen Iterationen die für ein Request berechnet werden.
     */
    private static final int MAX_ITERATIONS_PER_REQUEST = 10 * 60 * 30;
    /**
     * Das maximale Alter von Reqeusts in gameTicks, bevor sie verworfen werden.
     */
    private static final int MAX_REQUEST_AGE = 2000;
    /**
     * Gibt an wieviele Iterationen pro Aufruf berechnet werden.
     * Bestimmt, wi viel der Pathfinder pro gameTick rechnet,
     */
    private static final int MAX_ITERATIONS_PER_CYCLE = 30;
    /**
     * Warteschlange der Wegberechnungsanforderungen.
     */
    private LinkedList<PathRequest> pathRequests;
    /**
     * Das Request das gerade bearbeitet wird.
     */
    private PathRequest currentRequest;
    /**
     * Der eigentliche AStar-Algorithmus.
     */
    private AStarImplementation aStar;
    /**
     * Gibt an wieviele Iterationen für den aktuellen WEg schon berechnet wurden.
     */
    private int iterationsOfCurrentRequest;

    public AStarPathfinder() {
        pathRequests = new LinkedList<>();
        aStar = new AStarImplementation();
    }

    /**
     * Lässt den Pathfinder einen Weg berechnen. Diese Methode kehrt sofort zurück, der Weg wird im Hintergrund berechnet.
     * Wenn der Weg fertig berechnet ist wird er dem angegebenen requester geschickt.
     *
     * @param start das Startfeld des Pfads. Bei Size > 1 entspricht das dem linken oberen Feld.
     * @param target das Zielfeld. Bei Size > 1 ist das wieder das linke oebere Feld.
     * @param requester der PathRequester, der das Ergebnis der berechnung erhält.
     * @param size die Breite des Pfads in Feldern
     */
    public void requestPath(Position start, Position target, PathRequester requester, int size) {
        pathRequests.push(new PathRequest(start, target, requester, size, aStar));
    }

    /**
     * Berechnet 100 Iterationen der Wegfindungalle.
     * Wenn ein Weg fertig berechnet wird wird er seinem Requester übergeben.
     */
    public void computePaths() {
        int iterations = MAX_ITERATIONS_PER_CYCLE;
        while (iterations > 0) {
            iterations--;
            if (currentRequest != null) {
                if (currentRequest.isDone()) {
                    currentRequest = null;
                } else {
                    currentRequest.computeIteration();
                    iterationsOfCurrentRequest++;
                    if (iterationsOfCurrentRequest > MAX_ITERATIONS_PER_REQUEST) {
                        currentRequest.abort();
                        currentRequest = null;
                    }
                }
            } else {    
                Iterator<PathRequest> iter = pathRequests.iterator();
                while (iter.hasNext()) {
                    PathRequest request = iter.next();
                    if (request.getAge() < MAX_REQUEST_AGE) {
                        iter.remove();
                        currentRequest = request;
                        iterationsOfCurrentRequest = 0;
                        currentRequest.initialise();
                        // request geladen
                        break;
                    }
                    // keine requests mehr, abbrechen bis zum nächsten tick
                    return;
                }
            }
        }
    }
}
