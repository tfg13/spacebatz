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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.Movement;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Char {

    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints;
    /**
     * Die maximalen Lebenspunkte des Chars
     */
    protected int healthpointsmax;
    /**
     * Die netID.
     */
    public final int netID;
    /**
     * Die Startposition der Bewegung.
     */
    private double x, y;
    /**
     * Die Richtung, in die dieser Char schaut.
     * Die übliche PI-Einheitskreis-Zählweise
     */
    private double dir = 0;
    /**
     * Die Richtung der Bewegung.
     */
    private double vX, vY;
    /**
     * Die Geschwindigkeit der Bewegung. (0 bei Stillstand)
     */
    private double speed;
    /**
     * Der Tick, zu dem die Bewegung begonnen hat.
     */
    private int startTick;

    public Char(int netID) {
        this.netID = netID;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Liefert den aktuellen X-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle X-Position
     */
    public double getX() {
        return ((int) (16f * (x + ((Client.frozenGametick - startTick) * speed * vX)))) / 16f;
    }

    /**
     * Liefert den aktuellen Y-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle Y-Position
     */
    public double getY() {
        return ((int) (16f * (y + ((Client.frozenGametick - startTick) * speed * vY)))) / 16f;
    }

    /**
     * @return the dir
     */
    public double getDir() {
        return dir;
    }

    /**
     * Wendet eine Bewegung auf diese Einheit an.
     *
     * @param sX Startposition, X
     * @param sY Startposition, Y
     * @param vX Richtung, X (normiert!)
     * @param vY Richtung, Y (normiert!)
     * @param startTick Startzeitpunkt in Logik-Ticks
     * @param speed Bewegungsgeschwindigkeit
     */
    public void applyMove(Movement m) {
        x = m.startX;
        y = m.startY;
        this.vX = m.vecX;
        this.vY = m.vecY;
        this.startTick = m.startTick;
        this.speed = m.speed;
	// Nicht drehen beim Stehenbleiben
	if (startTick != -1) {
	    this.dir = Math.atan2(vY, vX);
	}
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Char) {
            Char c = (Char) o;
            return c.netID == this.netID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.netID;
        return hash;
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
    }

    /**
     * @param healthpoints the healthpoints to set
     */
    public void setHealthpoints(int healthpoints) {
        this.healthpoints = healthpoints;
    }

    /**
     * @return the healthpointsmax
     */
    public int getHealthpointsmax() {
        return healthpointsmax;
    }
}
