package de._13ducks.spacebatz.client;

/**
 * Ein Geschoss, dass vom Client gerendert werden muss
 * 
 * @author Johannes
 */
public class Bullet extends Char {
    /*
     * ID vom Bullettyp.
     */
    public final int bullettypeID;

    public Bullet(int netID, int bullettypeID) {
        super(netID);
        this.bullettypeID = bullettypeID;
    }
}
