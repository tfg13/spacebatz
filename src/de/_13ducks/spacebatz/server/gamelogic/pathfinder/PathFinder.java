/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.gamelogic.pathfinder;

import java.util.ArrayList;

/**
 * Berechnet Wege in der Spielwelt.
 *
 * @author michael
 */
public class PathFinder {

    /**
     * Das Level, dessen Kollision für die berechnung verwendet werden soll
     */
    private boolean[][] collisionMap;
    /**
     * Liste mit Wegfindungsaufträgen, die bearbeitet werden müssen
     */
    private ArrayList<PathRequest> pathRequests;

    /**
     * Konstruktor, initialisiert den Wegfinder
     *
     * @param collisionMap die Kollisionskarte, für die deer WEg berechnet wird
     */
    public PathFinder(boolean[][] collisionMap) {
        this.collisionMap = collisionMap;
    }

    /**
     * Registriert eine neue Wegberechnungsanforderung zur Bearbeitung.
     *
     * @param requester Der PathRequester, der benachrichtigt wird wenn der Weg berechent ist
     */
    public void requestPath(PathRequester requester) {
        pathRequests.add(new PathRequest(requester, collisionMap));
    }

    /**
     * Bearbeitet die Wegberechnungsanforderungen.
     */
    public void computePaths() {
        while (!pathRequests.isEmpty()) {
            // solange anforderungen da sin, berechnen:
            if (pathRequests.get(0).computePath()) {
                // wenn eine anforderung fertig berechnet ist entfernen:
                pathRequests.remove(0);
            }
        }
    }
}
