package de._13ducks.spacebatz.util.geo;

/**
 * Allgemeine Geometrische Hilfsfunktionen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class GeoTools {

    private static final double twoPI = Math.PI * 2;

    /**
     * Private, da Utility-Class
     */
    private GeoTools() {
    }

    /**
     * Liefert den Winkel zur Horziontalen eines Vektors, der vom Nullpunkt zu den angegebenen Koordinaten zeigt.
     *
     * @param x X-Koordinate
     * @param y Y-Koordinate
     * @return Winkel von 0 bis 2*PI
     */
    public static double toAngle(double x, double y) {
        double arc = Math.atan2(y, x);
        if (arc < 0) {
            arc += twoPI;
        }
        return arc;
    }

    /**
     * Liefert den Winkel zu Horziontalen des gegebenen Richtungsvektors.
     *
     * @param v der Richtungsvektor
     * @return der Winkel des Vektors v zur Horziontalen
     */
    public static double toAngle(Vector v) {
        return toAngle(v.x, v.y);
    }

    /**
     * Berechnet die Distanz zwischen zwei Punkten
     * @param x1 X-Koordinate des ersten Punkts
     * @param y1 Y-Koordinate des ersten Punkts
     * @param x2 X-Koordinate des zweiten Punkts
     * @param y2 Y-Koordinate des zweiten Punkts
     * @return die Distanz der Punkte als double
     */
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
