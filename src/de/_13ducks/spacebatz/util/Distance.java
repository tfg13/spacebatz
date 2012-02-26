/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.util;

/**
 * Hilfsklasse zum berechnen des Abstandes zweier Punkte.
 * @author michael
 */
public final class Distance {

    /**
     * Berechnet die Distanz zwischen zwei Punkten
     * @param x1 X-Koordinate des ersten Punkts
     * @param y1 Y-Koordinate des ersten Punkts
     * @param x2 X-Koordinate des zweiten Punkts
     * @param y2 Y-Koordinate des zweiten Punkts
     * @return die Distanz der Punkte als double
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return (Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
    }
}
