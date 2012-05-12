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
 * Interface für eine einfache 2D-Kollisionskarte
 *
 * @author michael
 */
public interface CollisionMap {

    /**
     * Gibt an, ob das gegebene Feld Kollision hat oder nicht.
     *
     * @param x X-Koordinate des Feldes
     * @param y Y-Koordinate des Feldes
     * @return true, wenn Kollision auf dem feld gesetzt ist, oder false wenn nicht
     */
    public boolean getCollision(int x, int y);

    /**
     * Gibt die Breite der Kollisionskarte zurück.
     *
     * @return die Breite der Kollisionskarte
     */
    public int getWidth();

    /**
     * Gibt die Höhe der Kollisionskarte zurück.
     *
     * @return die Höhe der Kollisionskarte
     */
    public int getHeight();
}
