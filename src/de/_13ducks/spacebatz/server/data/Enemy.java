package de._13ducks.spacebatz.server.data;

/**
 * Ein Gegner.
 * @author J
 */
public class Enemy extends Char {

    /**
     * Die normale Geschwindigkeit vom Enemy.
     */
    private double speed = .1;
    /**
     * ID des Gegnertyps
     */
    private int enemytypeid = 1;
    /**
     * Der Char, den dieser Enemy gerade verfolgt
     */
    private Char myTarget;

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

    /**
     * Gibt den Char, den dieser Enemy gerade verfolgt, zurück.
     * @return der Char der gerade verfolgt wird
     */
    public Char getMyTarget() {
        return myTarget;
    }

    /**
     * Setzt den Char, den dieser Enemy gerade verfolgt.
     * @param der Char den dieser Enemy verfolgen soll
     */
    public void setMyTarget(Char myTarget) {
        this.myTarget = myTarget;
    }
}
