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
     * Warteschlange der Wegberechnungsanforderungen.
     */
    private LinkedList<PathRequest> pathRequests;
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
     * Berechnet alle Wege. Rechenzeitaufwändig.
     */
    public void computePaths() {
        while (!pathRequests.isEmpty()) {
            PathRequest request = pathRequests.pop();
            aStar.loadPathRequest(request.getStart(), request.getGoal(), request.getRequester());
            while (!aStar.isComputed()) {
                aStar.computeIteration();
            }
        }

    }
}
