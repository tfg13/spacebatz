package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Ein Punkt des Pfades einer Entity. Enthält Position und Bewegungsrichtung der Entity, als sie an dieser Position war. 
 * @author michael
 */
public class PathNode extends Vector {

    public double vecX;
    public double vecY;

    public PathNode(double x, double y, double vecX, double vecY) {
        super(x, y);
        this.vecX = vecX;
        this.vecY = vecY;
    }
}
