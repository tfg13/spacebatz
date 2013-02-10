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
package de._13ducks.spacebatz.util.geo;

/**
 * Erzeugt eine neuen Int-Vector
 *
 * @author Jojo
 */
public class IntVector {

    public final int x;
    public final int y;

    public IntVector(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IntVector && ((IntVector) obj).x == x && ((IntVector) obj).y == y);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.x;
        hash = 53 * hash + this.y;
        return hash;
    }

    @Override
    public String toString() {
        return "Position " + x + " " + y;
    }
}
