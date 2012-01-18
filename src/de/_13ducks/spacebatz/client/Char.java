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
     * Die Koordinaten.
     */
    private double x, y;
    /**
     * Die Richtung, in die dieser Char schaut. (Tilemapsynchron)
     * Automatisch bei setX/Y gesetzt.
     */
    private int dir = 0;
    
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

}
