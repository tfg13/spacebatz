package de._13ducks.spacebatz.server.ai.behaviour;

import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 *
 * @author michael
 */
public class Divergation {

    /**
     * Berechnet einen Vektor, der die Entity an der gegebenen Position von allen anderen gegebenen Positionen weggehen lässt.
     *
     * @param position die Üosition der Entity , für die der Vektor berechnet werden soll.
     * @param otherPositions die Positionen, von denen die Entity sich entfernen soll.
     * @return ein Vektor, der die Entity von den Positionen wegbewegt.
     */
    public static Vector computeDivergationVector(Vector position, Vector otherPositions[]) {
        Vector divergation = new Vector(0, 0);
        for (int i = 0; i < otherPositions.length; i++) {
            Vector singleDivergation = new Vector(2 * position.x - otherPositions[i].x, 2 * position.y - otherPositions[i].y);
            divergation = divergation.add((singleDivergation));
        }
        return divergation;
    }
}
