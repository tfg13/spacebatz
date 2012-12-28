package de._13ducks.spacebatz.util.mapgen.data;

/**
 * Rechteck-Klasse.
 * Für verbesserte Performance
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Rect {

    public final double smallX, largeX, smallY, largeY;

    public Rect(double smallX, double largeX, double smallY, double largeY) {
        this.smallX = smallX;
        this.smallY = smallY;
        this.largeX = largeX;
        this.largeY = largeY;
    }
}
