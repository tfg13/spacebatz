package de._13ducks.spacebatz.client;

/**
 * Ein Geschoss, dass vom Client gerendert werden muss
 * 
 * @author Johannes
 */
public class Bullet extends Char {
    /*
     * Bild
     */
    public final int bulletpic;

    public Bullet(int netID, int bullettypeID) {
        super(netID);
        this.bulletpic = bullettypeID;
    }
}
