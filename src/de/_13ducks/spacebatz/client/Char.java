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
     * Liefert den aktuellen X-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle X-Position
     */
    public double getX() {
        return x + ((Client.gametick - startTick) * speed * vX);
    }

    /**
     * Liefert den aktuellen Y-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle Y-Position
     */
    public double getY() {
        return y + ((Client.gametick - startTick) * speed * vY);
    }

    /**
     * @return the dir
     */
    public int getDir() {
        return dir;
    }

    /**
     * Wendet eine Bewegung auf diese Einheit an.
     *
     * @param sX Startposition, X
     * @param sY Startposition, Y
     * @param vX Richtung, X (normiert!)
     * @param vY Richtung, Y (normiert!)
     * @param startTick Startzeitpunkt in Logik-Ticks
     * @param speed Bewegungsgeschwindigkeit
     */
    public void applyMove(double sX, double sY, double vX, double vY, int startTick, double speed) {
        x = sX;
        y = sY;
        this.vX = vX;
        this.vY = vY;
        this.startTick = startTick;
        this.speed = speed;
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
