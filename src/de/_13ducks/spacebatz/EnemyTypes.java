package de._13ducks.spacebatz;

import java.util.ArrayList;

/**
 *
 * @author ojoj
 */
public class EnemyTypes implements java.io.Serializable{
    private ArrayList<EnemyTypeStats> enemytypelist;
    
    public EnemyTypes() {
        enemytypelist = new ArrayList<>();
        enemytypelist.add(new EnemyTypeStats(120, 5, 0, 0.1f, 11));
        enemytypelist.add(new EnemyTypeStats(12, 1, 1, 0.12f, 8));
    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
