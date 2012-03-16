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
package de._13ducks.spacebatz.shared;

import java.util.ArrayList;

/**
 * Enthält eine Liste mit Werten der verschiedenen Bulletsorten
 * Wird evtl gelöscht, Bulletstats kommen zur Waffe
 * @author bla
 */
public class BulletTypes implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<BulletTypeStats> bullettypelist;
    
    public BulletTypes() {
        bullettypelist = new ArrayList<>();
        // damage, picture, numberofhits, speed, spread
        bullettypelist.add(new BulletTypeStats(3, 2, 1, 0.25f, (float) Math.PI / 80, 60));
        bullettypelist.add(new BulletTypeStats(1, 2, 1, 0.15f, (float) 0, 60));
    }

    /**
     * @return the bullettypelist
     */
    public ArrayList<BulletTypeStats> getBullettypelist() {
        return bullettypelist;
    }
}
