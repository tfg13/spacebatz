/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client;

/**
 * Ein Gegner.
 * @author Joj
 */
public class Enemy extends Char {
    /**
     * ID des Gegnertyps
     */
    private int enemytypeid = 1;

    public Enemy(int netID, int enemytypeid) {
        super(netID);
        this.enemytypeid = enemytypeid;
    }

    /**
     * @return the enemytypeid
     */
    public int getEnemytypeid() {
        return enemytypeid;
    }
}
