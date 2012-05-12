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

/**
 * Ein Wegberechnungsanforderer.
 *
 * Interface für alle Klassen, die vom Pathfinder Wege berechnen lassen.
 *
 * @author michael
 */
public interface PathRequester {

    /**
     * Wird vom Pathfinder aufgerufen, wenn ein angeforderter Weg berechnet wurde.
     *
     * @param way der berechnete Weg
     */
    public void receivePath(Object way);
}
