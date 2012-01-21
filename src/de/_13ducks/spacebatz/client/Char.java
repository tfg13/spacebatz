package de._13ducks.spacebatz.client;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Char {

    /**
     * Die netID.
     */
    public final int netID;
    /**
     * Die Startposition der Bewegung.
     */
    private double x, y;
    /**
     * Die Richtung, in die dieser Char schaut. (Tilemapsynchron) Automatisch bei setX/Y gesetzt.
     */
    private int dir = 0;
    /**
     * Die Richtung der Bewegung.
     */
    private double vX, vY;
    /**
     * Die Geschwindigkeit der Bewegung. (0 bei Stillstand)
     */
    private double speed;
    /**
     * Der Tick, zu dem die Bewegung begonnen hat.
     */
    private int startTick;

    public Char(int netID, double x, double y) {
        this.netID = netID;
        this.x = x;
        this.y = y;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Setzt X und gegebenenfalls die neue Richtung.
     *
     * @param x the x to set
     */
    public void setX(double x) {
        if (this.x < x) {
            dir = 4;
        } else if (this.x > x) {
            dir = 0;
        }
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Setzt Y und gegebenenfalls die neue Richtung.
     *
     * @param y the y to set
     */
    public void setY(double y) {
        if (this.y < y) {
            dir = 6;
        } else if (this.y > y) {
            dir = 2;
        }
        this.y = y;
    }

    /**
     * @return the dir
     */
    public int getDir() {
        return dir;
    }

    /**
     * @param dir the dir to set
     */
    public void setDir(int dir) {
        this.dir = dir;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Char) {
            Char c = (Char) o;
            return c.netID == this.netID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.netID;
        return hash;
    }
}
