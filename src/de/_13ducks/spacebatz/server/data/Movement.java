package de._13ducks.spacebatz.server.data;

/**
 * Repräsentiert eine Bewegung einer Einheit. Für die Netzwerksychronisierung notwendig.
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
     * Der startTick der Bewegung. Wenn -1 wird ein Stehen repräsentiert.
     */
    public final int startTick;
    /**
     * Die Geschwindigkeit der Bewegung.
     */
    public final double speed;

    public Movement(double startX, double startY, double vecX, double vecY, int startTick, double speed) {
        this.startX = startX;
        this.startY = startY;
        this.vecX = vecX;
        this.vecY = vecY;
        this.startTick = startTick;
        this.speed = speed;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Movement) {
            Movement m = (Movement) o;
            // Wenn zwei stehende Positionen verglichen werden sind Richtung und Geschwindigkeit egal.
            if (this.startTick == -1 && m.startTick == -1) {
                return (m.startX == this.startX && m.startY == m.startY);
            }
            // Bewegt sich, alles vergleichen.
            return (m.startX == this.startX && m.startY == this.startY && m.vecX == this.vecX && m.vecY == this.vecY && m.startTick == this.startTick && m.speed == this.speed);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Wie bei equals zwischen stehend und in Bewegung unterscheiden.
        if (startTick == -1) {
            int hash = 3;
            hash = 83 * hash + (int) (Double.doubleToLongBits(this.startX) ^ (Double.doubleToLongBits(this.startX) >>> 32));
            hash = 83 * hash + (int) (Double.doubleToLongBits(this.startY) ^ (Double.doubleToLongBits(this.startY) >>> 32));
            return hash;
        } else {
            int hash = 3;
            hash = 37 * hash + (int) (Double.doubleToLongBits(this.startX) ^ (Double.doubleToLongBits(this.startX) >>> 32));
            hash = 37 * hash + (int) (Double.doubleToLongBits(this.startY) ^ (Double.doubleToLongBits(this.startY) >>> 32));
            hash = 37 * hash + (int) (Double.doubleToLongBits(this.vecX) ^ (Double.doubleToLongBits(this.vecX) >>> 32));
            hash = 37 * hash + (int) (Double.doubleToLongBits(this.vecY) ^ (Double.doubleToLongBits(this.vecY) >>> 32));
            hash = 37 * hash + this.startTick;
            hash = 37 * hash + (int) (Double.doubleToLongBits(this.speed) ^ (Double.doubleToLongBits(this.speed) >>> 32));
            return hash;
        }
    }
}
