package de._13ducks.spacebatz.util.mapgen.data;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Ein verbesserter Polygon, der zusätzliche Daten speichern kann.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MPolygon extends Polygon {

    private static final long serialVersionUID = 1L;

    public MPolygon(LinearRing shell, LinearRing[] holes, GeometryFactory factory) {
        super(shell, holes, factory);
    }
    /**
     * Ob dieser Polygon zum Rand gehört und daher auf keinen Fall komplett auf "frei" gesetzt werden darf.
     */
    public boolean border = false;
    /**
     * Ob dieser Polygon ein Berg ist, oder Freifläche.
     */
    public boolean solid = false;
}
