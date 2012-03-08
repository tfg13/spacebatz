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
