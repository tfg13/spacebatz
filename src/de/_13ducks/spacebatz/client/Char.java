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
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

}
