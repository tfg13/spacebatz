package de._13ducks.spacebatz.server.ai.astar;

import de._13ducks.spacebatz.util.Position;
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
     * Gibt an wieviele Iterationen pro Aufruf berechnet werden.
     * Bestimmt, wi viel der Pathfinder pro gameTick rechnet,
     */
    private static final int MAX_ITERATIONS_PER_CYCLE = 10;
    /**
     * Warteschlange der Wegberechnungsanforderungen.
     */
    private LinkedList<PathRequest> pathRequests;
    /**
     * Der eigentliche AStar-Algorithmus.
     */
    private AStarImplementation aStar;

    public AStarPathfinder() {
        pathRequests = new LinkedList<>();
        aStar = new AStarImplementation();
    }

    /**
     * Lässt den Pathfinder einen Weg berechnen. Diese Methode kehrt sofort zurück, der Weg wird im Hintergrund berechnet.
     * Wenn der Weg fertig berechnet ist wird er dem angegebenen requester geschickt.
     *
     * @param requestedPath der Weg der berechnet werden soll
     * @param requester der PathRequester, der das Ergebnis der berechnung erhält.
     */
    public void requestPath(Position start, Position target, PathRequester requester) {
        pathRequests.push(new PathRequest(start, target, requester));
    }

    /**
     * Berechnet 100 Iterationen der Wegfindungalle.
     * Wenn ein Weg fertig berechnet wird wird er seinem Requester übergeben.
     */
    public void computePaths() {
        int iterations = MAX_ITERATIONS_PER_CYCLE;
        while (iterations > 0) {
            iterations--;
            if (aStar.isComputed()) {
                if (!pathRequests.isEmpty()) {
                    // nächste Wegberechnung vorbereiten:
                    PathRequest request = pathRequests.pop();
                    aStar.loadPathRequest(request.getStart(), request.getGoal(), request.getRequester());
                } else {
                    // Keine Wege mehr zum berechnen
                    return;
                }
            } else {
                // am aktuellen Weg rechnen:
                aStar.computeIteration();
            }
        }
    }
}
