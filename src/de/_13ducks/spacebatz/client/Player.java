/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.Settings;

/**
 * Der eigene Spieler
 * 
 * @author JK
 */
public class Player extends Char {

    /**
     * Die gerade ausgewählte Waffe
     */
    private int selectedattack;
    
    public Player(int netID) {
        super(netID);
        selectedattack = 0;
        healthpoints = Settings.CHARHEALTH;
        healthpointsmax = Settings.CHARHEALTH;
    }

    /**
     * @return the selectedattack
     */
    public int getSelectedattack() {
        return selectedattack;
    }

    /**
     * @param selectedattack the selectedattack to set
     */
    public void setSelectedattack(int selectedattack) {
        this.selectedattack = selectedattack;
    }
}
