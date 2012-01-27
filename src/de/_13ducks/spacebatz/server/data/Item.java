package de._13ducks.spacebatz.server.data;

/**
 *
 * @author Jojo
 */
public class Item {

    /**
     * Tpy des Items.
     */
    public byte itemTypeID;
    /**
     * Die ID des Items.
     */
    public int netID;
    /*
     * Ort, an dem das Item erstellt wurde
     */
    private double posX;
    private double posY;
    
    public Item(double posX, double posY, byte itemTypeID, int netID) {
        this.posX = posX;
        this.posY = posY;
        this.itemTypeID = itemTypeID;
        this.netID = netID;
    }

    /**
     * @return the posX
     */
    public double getPosX() {
        return posX;
    }

    /**
     * @return the posY
     */
    public double getPosY() {
        return posY;
    }
}
