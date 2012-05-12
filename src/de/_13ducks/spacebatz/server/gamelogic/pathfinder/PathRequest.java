/*
 * Copyright 2011, 2012:
 * Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 * Michael Haas (mekhar[AT]gmx[DOT]de)
 * Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.gamelogic.pathfinder;

/**
 * Verwaltet eine Wegberechnung.
 *
 * @author michael
 */
public class PathRequest {

    /**
     * Der Anfordere, der die WEgberechnung angefordert hat
     */
    private PathRequester requester;
    /**
     * Die Kollisionskarte, für die der weg berechnet wird
     */
    boolean[][] colMap;

    /**
     * Konstrunktor, erzeug eine neue Anforderung.
     *
     * @param requester der Anfoderer, der den Weg berechnet haben will
     * @param colMap die Kollisionskarte als bool-array
     */
    public PathRequest(PathRequester requester, boolean[][] colMap) {
        this.requester = requester;
        this.colMap = colMap;
    }

    /**
     * Berechnet eine Iteration der Wegfindung.
     * Wenn der Weg fertig berechnet ist, wird true zurückgegeben, ansonsten false.
     *
     * @return true wenn die berechnung fertig ist, sonst false
     */
    public boolean computePath() {
        return false;
    }
}