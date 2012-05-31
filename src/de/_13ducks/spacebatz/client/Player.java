/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;

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
        super(netID, new RenderObject(new Animation(0, 2, 2, 1, 1)));
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
