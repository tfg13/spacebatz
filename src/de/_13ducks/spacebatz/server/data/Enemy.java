package de._13ducks.spacebatz.server.data;

/**
 * Ein Gegner.
 * @author J
 */
public class Enemy extends Char {

    /**
     * Die normale Geschwindigkeit dieses Players.
     */
    private double speed = .1;
    /**
     * ID des Gegnertyps
     */
    private int enemytypeid = 1;
    
    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     */
    public Enemy(double x, double y, int id) {
        super(x, y, id);
    }
}
