package de._13ducks.spacebatz.server.data;

/**
 * Repräsentiert eine Bewegung einer Einheit.
 * Für die Netzwerksychronisierung notwendig.
 * 
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Movement {
    
    /**
     * Die Startposition der Bewegung.
     */
    public final double startX, startY;
    
    /**
     * Die Richtung der Bewegung, normalisiert.
     */
    public final double vecX, vecY;
    
    /**
     * Der startTick der Bewegung.
     */
    public final int startTick;
    
    public Movement(double startX, double startY, double vecX, double vecY, int startTick) {
        this.startX = startX;
        this.startY = startY;
        this.vecX = vecX;
        this.vecY = vecY;
        this.startTick = startTick;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Movement) {
            Movement m = (Movement) o;
            return (m.startX == this.startX && m.startY == this.startY && m.vecX == this.vecX && m.vecY == this.vecY && m.startTick == this.startTick);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.startX) ^ (Double.doubleToLongBits(this.startX) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.startY) ^ (Double.doubleToLongBits(this.startY) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.vecX) ^ (Double.doubleToLongBits(this.vecX) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.vecY) ^ (Double.doubleToLongBits(this.vecY) >>> 32));
        hash = 73 * hash + this.startTick;
        return hash;
    }

}
