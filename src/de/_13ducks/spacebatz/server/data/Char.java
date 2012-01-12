package de._13ducks.spacebatz.server.data;

/**
 * Ein bewegliches Objekt. (z.B. ein Spieler, Mob etc)
 *
 * @author michael
 */
public class Char {

    /**
     * Die Position des Chars.
     */
    protected double posX, posY;
    /**
     * Die ID des Chars.
     */
    public final int netID;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x
     * @param y
     * @param name
     */
    public Char(double x, double y, int id) {
        this.posX = x;
        this.posY = y;
        this.netID = id;
    }

    /**
     * Liefert die X-Koordinate.
     *
     * @return die X-Koordinate.
     */
    public double getX() {
        return posX;
    }

    /**
     * Liefert die Y-Koordinate.
     *
     * @return die Y-Koordinate.
     */
    public double getY() {
        return posY;
    }
}
