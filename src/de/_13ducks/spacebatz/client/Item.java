/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client;

/**
 *
 * @author J.K.
 */
public class Item {

    /**
     * Typ des Items.
     */
    public byte itemTypeID;
    /**
     * Die ID des Items.
     */
    public int netID;
    /*
     * Ort, an dem das Item erstellt wurde
     */
    private float posX;
    private float posY;

    public Item(float posX, float posY, byte itemTypeID, int netID) {
        this.posX = posX;
        this.posY = posY;
        this.itemTypeID = itemTypeID;
        this.netID = netID;
    }

    /**
     * @return the posX
     */
    public float getPosX() {
        return posX;
    }

    /**
     * @return the posY
     */
    public float getPosY() {
        return posY;
    }
}
