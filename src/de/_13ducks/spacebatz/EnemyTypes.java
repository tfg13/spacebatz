package de._13ducks.spacebatz;

import java.util.ArrayList;

/**
 *
 * @author ojoj
 */
public class EnemyTypes {
    private ArrayList<EnemyTypeStats> enemytypelist;
    
    public EnemyTypes() {
        enemytypelist = new ArrayList<>();
        enemytypelist.add(new EnemyTypeStats(180, 5, 0));
        enemytypelist.add(new EnemyTypeStats(12, 1, 1));
    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
