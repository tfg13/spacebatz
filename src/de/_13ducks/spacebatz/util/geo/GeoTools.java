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
}
