package de._13ducks.spacebatz.server.ai.astar;

/**
 *
 * @author michael
 */
public class PrecisePosition {

    private double x, y;

    public PrecisePosition(double x, double y) {
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

    @Override
    public String toString() {
        return ("Pos:" + getX() + " " + getY());
    }
}
