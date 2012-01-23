package de._13ducks.spacebatz.server.data;

import java.io.Serializable;

/**
 * Beschreibt eine Kollisionsfläche, z.B. eine Mauer, an der Bullets und Chars etc kollidieren
 * @author michael
 */
public class Wall implements Serializable {

    /**
     * Koordinaten der linken oberen Ecke
     */
    private double startX, startY;
    /**
     * Koordinaten der rechten unteren Ecke
     */
    private double endX, endY;

    /**
     * Erstellt ein neues Wall-Objekt mit den angegebenen Positionen
     * Die Positionen entsprechen der linken oberen und der rechten unteren Ecke der Fläche
     * @param x1 X-Koordinate der linken obreren Ecke 
     * @param y1 Y-Koordinate der linken obreren Ecke 
     * @param x2 X-Koordinate der rechten unteren Ecke 
     * @param y2 Y-Koordinate der rechten unteren Ecke 
     */
    public Wall(double x1, double y1, double x2, double y2) {
        this.startX = x1;
        this.startY = y1;
        this.endX = x2;
        this.endY = y2;
    }

    /**
     * Prüft, ob ein Punkt in dieser wand enthalten ist
     * @param x1 X-Koordinate des zu überprüfenden Punkts
     * @param y1 Y-Koordinate des zu überprüfenden Punkts
     * @return true, wenn der Punkt in dieser Wand liegt
     */
    public boolean containsPoint(double x1, double y1) {
        return (((startX < x1 && x1 < endX) || (startX > x1 && x1 > endX)) && ((startY > y1 && y1 > endY) || (startY < y1 && y1 < endY)));
    }
}
