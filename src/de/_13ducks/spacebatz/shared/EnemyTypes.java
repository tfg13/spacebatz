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
 * Enthält eine Liste mit Werten der verschiedenen Gegnersorten
 * @author ojoj
 */
public class EnemyTypes implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<EnemyTypeStats> enemytypelist;
    
    public EnemyTypes() {
        enemytypelist = new ArrayList<>();
        //int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel        
        enemytypelist.add(new EnemyTypeStats(12, 3, 0, 0.12f, 8, 8));
        enemytypelist.add(new EnemyTypeStats(60, 5, 1, 0.1f, 11, 4));
        enemytypelist.add(new EnemyTypeStats(25, 7, 2, 0.17f, 8, 6));
        enemytypelist.add(new EnemyTypeStats(100, 7, 3, 0.20f, 8, 8));
    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
