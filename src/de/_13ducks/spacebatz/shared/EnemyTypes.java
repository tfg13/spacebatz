package de._13ducks.spacebatz.shared;

import java.util.ArrayList;

/**
 * Enthält eine Liste mit Werten der verschiedenen Gegnersorten
 * @author ojoj
 */
public class EnemyTypes implements java.io.Serializable{
    private ArrayList<EnemyTypeStats> enemytypelist;
    
    public EnemyTypes() {
        enemytypelist = new ArrayList<>();
        //int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel
        enemytypelist.add(new EnemyTypeStats(120, 5, 0, 0.1f, 11, 4));
        enemytypelist.add(new EnemyTypeStats(12, 1, 1, 0.12f, 8, 8));
    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
