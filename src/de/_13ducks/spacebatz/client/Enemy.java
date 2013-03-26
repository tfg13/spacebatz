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

import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;

/**
 * Ein Gegner.
 *
 * @author Joj
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps
     */
    private int enemytypeid = 1;

    public Enemy(int netID, float size, int enemytypeid) {
        super(netID, size, new RenderObject(new Animation(GameClient.enemytypes.getEnemytypelist().get(enemytypeid).getPicture(), 2, 2, 4, 1)));
        this.enemytypeid = enemytypeid;
    }

    /**
     * @return the enemytypeid
     */
    public int getEnemytypeid() {
        return enemytypeid;
    }
}
