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
public class EnemyTypes implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<EnemyTypeStats> enemytypelist;

    public EnemyTypes() {
        enemytypelist = new ArrayList<>();
        //int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel, (Farbe in floats)

        EnemyTypeStats smallShooter = new EnemyTypeStats();
        enemytypelist.add(smallShooter);
        smallShooter.setBehaviour(EnemyTypeStats.BehaviourType.SHOOTER);
        smallShooter.setShootAbility(EnemyTypeStats.AbilityType.FIREBULLET);
        smallShooter.setHealthpoints(12);
        smallShooter.setDamage(1);
        smallShooter.setPicture(0);
        smallShooter.setSpeed(0.15f);
        smallShooter.setSightrange(5);
        smallShooter.setEnemylevel(2);

        EnemyTypeStats bigShooter = new EnemyTypeStats();
        enemytypelist.add(bigShooter);
        bigShooter.setBehaviour(EnemyTypeStats.BehaviourType.SHOOTER);
        bigShooter.setShootAbility(EnemyTypeStats.AbilityType.FIREBULLET);
        bigShooter.setHealthpoints(30);
        bigShooter.setDamage(5);
        bigShooter.setPicture(24);
        bigShooter.setSpeed(0.1f);
        bigShooter.setSightrange(5);
        bigShooter.setEnemylevel(3);
        
        EnemyTypeStats spectator = new EnemyTypeStats();
        enemytypelist.add(spectator);
        spectator.setBehaviour(EnemyTypeStats.BehaviourType.SPECTATOR);
        spectator.setShootAbility(EnemyTypeStats.AbilityType.NONE);
        spectator.setHealthpoints(10);
        spectator.setPicture(16);
        spectator.setSpeed(0.3f);
        spectator.setSightrange(5);
        spectator.setEnemylevel(1);


//
//        enemytypelist.add(new EnemyTypeStats(12, 3, 0, 0.12f, 8, 1));
//        enemytypelist.add(new EnemyTypeStats(60, 5, 1, 0.1f, 11, 4, 1f, 0f, 0f, 1f));
//        enemytypelist.add(new EnemyTypeStats(25, 7, 2, 0.17f, 8, 6));
//        enemytypelist.add(new EnemyTypeStats(100, 7, 3, 0.20f, 8, 10, 1f, 1f, 1f, 0.35f));
    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
