package de._13ducks.spacebatz.shared;

/**
 * Repräsentiert eine Bewegung einer Einheit. Für die Netzwerksychronisierung notwendig.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Movement {

    /**
     * Die Startposition der Bewegung.
     */
    public final float startX, startY;
    /**
     * Die Richtung der Bewegung, normalisiert.
     */
    public final float vecX, vecY;
    /**
     * Der startTick der Bewegung. Wenn -1 wird ein Stehen repräsentiert.
     */
    public final int startTick;
    /**
     * Die Geschwindigkeit der Bewegung.
     */
    public final float speed;

    public Movement(double startX, double startY, double vecX, double vecY, int startTick, double speed) {
        this.startX = (float) startX;
        this.startY = (float) startY;
        this.vecX = (float) vecX;
        this.vecY = (float) vecY;
        this.startTick = startTick;
        this.speed = (float) speed;
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
        if (startTick == -1) {
            int hash = 5;
            hash = 47 * hash + Float.floatToIntBits(this.startX);
            hash = 47 * hash + Float.floatToIntBits(this.startY);
            return hash;
        } else {
            int hash = 5;
            hash = 73 * hash + Float.floatToIntBits(this.startX);
            hash = 73 * hash + Float.floatToIntBits(this.startY);
            hash = 73 * hash + Float.floatToIntBits(this.vecX);
            hash = 73 * hash + Float.floatToIntBits(this.vecY);
            hash = 73 * hash + this.startTick;
            hash = 73 * hash + Float.floatToIntBits(this.speed);
            return hash;
        }
    }
}
