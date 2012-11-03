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
package de._13ducks.spacebatz.shared;

/**
 * Repräsentiert eine Bewegung einer Einheit. Für die Netzwerksychronisierung notwendig.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Movement {

    /**
     * Die Startposition der Bewegung.
     */
    public float startX, startY;
    /**
     * Die Richtung der Bewegung, normalisiert.
     */
    public float vecX, vecY;
    /**
     * Der startTick der Bewegung. Wenn -1 wird ein Stehen repräsentiert.
     */
    public int startTick;
    /**
     * Die Geschwindigkeit der Bewegung.
     */
    public float speed;
    /**
     * Gibt an ob dies eine normale Bewegung ist oder eine Verfolgungsbewegung.
     */
    public boolean followMode;
    /**
     * Die netID der Entity die im erfolgermodus verfolgt wird.
     * Nur gültig wenn followMode gesetzt ist.
     */
    public int targetId;

    public Movement(double startX, double startY, double vecX, double vecY, int startTick, double speed, boolean followMode, int targetId) {
        this.startX = (float) startX;
        this.startY = (float) startY;
        this.vecX = (float) vecX;
        this.vecY = (float) vecY;
        this.startTick = startTick;
        this.speed = (float) speed;
        this.followMode = followMode;
        this.targetId = targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Movement) {
            Movement m = (Movement) o;
            // Wenn zwei stehende Positionen verglichen werden sind Richtung und Geschwindigkeit egal.
            if (this.startTick == -1 && m.startTick == -1) {
                return (m.startX == this.startX && m.startY == m.startY);
            }
            // Bewegt sich, alles vergleichen.
            return (m.startX == this.startX && m.startY == this.startY && m.vecX == this.vecX && m.vecY == this.vecY && m.startTick == this.startTick && m.speed == this.speed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (startTick == -1) {
            int hash = 5;
            hash = 47 * hash + Float.floatToIntBits(this.startX);
            hash = 47 * hash + Float.floatToIntBits(this.startY);
            return hash;
        } else {
            int hash = 5;
            hash = 73 * hash + Float.floatToIntBits(this.startX);
            hash = 73 * hash + Float.floatToIntBits(this.startY);
            hash = 73 * hash + Float.floatToIntBits(this.vecX);
            hash = 73 * hash + Float.floatToIntBits(this.vecY);
            hash = 73 * hash + this.startTick;
            hash = 73 * hash + Float.floatToIntBits(this.speed);
            return hash;
        }
    }
}
