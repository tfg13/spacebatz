package de._13ducks.spacebatz.server.data;

import java.awt.Shape;
import java.util.ArrayList;

/**
 * Ein Gebiet auf der Map
 *
 * @author michael
 */
public class Area {

    /**
     * Die Liste der Formen, die das Gebiet bilden
     */
    private ArrayList<Shape> shapes;
    /**
     * Der Name, der das GEbiet identifiziert
     */
    private String name;

    /**
     * Erstellt ein neues Gebiet
     *
     * @param name
     */
    public Area(String name) {
        this.name = name;

    }

    /**
     * Fügt dem Gebiet eine neue Shape hinzu
     *
     * @param shape die neue Shape
     */
    public void addShape(Shape shape) {
        shapes.add(shape);
    }

    /**
     * Gibt den Namen des Gebiets zurück
     *
     * @return der Name des Gebiets
     */
    public String getName() {
        return name;
    }

    /**
     * Gibt true zurück wenn der angegebene Punkt enthalten ist
     *
     * @param x die X-Koordinate des Punkts
     * @param y die Y-Koordinate des Punkts
     * @return true wenn der Punkt enthalten ist, false wenn nicht
     */
    public boolean contains(double x, double y) {
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
}
