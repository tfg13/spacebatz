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
package de._13ducks.spacebatz.server.levelgenerator;

import de._13ducks.spacebatz.shared.Position;
import java.util.ArrayList;

/**
 * Kreisähnliche Polygon-Gebilde, stellen begehbaren Teil der Map dar
 * @author Jojo
 */
public class Circle {
    private Position center; // Zentrum des "Kreises"
    private ArrayList<Position> shape; // Randpunkte, 2 benachbarte spannen mit Zentrum ein Dreieck auf
    
    public Circle(Position center, ArrayList<Position> shape) {
     this.center = center;
     this.shape = shape;
    }

    /**
     * @return the center
     */
    public Position getCenter() {
        return center;
    }

    /**
     * @return the shape
     */
    public ArrayList<Position> getShape() {
        return shape;
    }
}
