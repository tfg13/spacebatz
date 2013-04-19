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
 *
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
        smallShooter.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        smallShooter.shootAbility =(EnemyTypeStats.AbilityType.FIREBULLET);
        smallShooter.healthpoints =(12);
        smallShooter.damage =(1);
        smallShooter.picture =(0);
        smallShooter.speed =(0.1f);
        smallShooter.sightrange =(5);
        smallShooter.enemylevel =(2);

        EnemyTypeStats bigShooter = new EnemyTypeStats();
        enemytypelist.add(bigShooter);
        bigShooter.behaviour =(EnemyTypeStats.BehaviourType.SHOOTER);
        bigShooter.shootAbility =(EnemyTypeStats.AbilityType.FIREBULLET);
        bigShooter.healthpoints =(30);
        bigShooter.damage =(5);
        bigShooter.picture =(24);
        bigShooter.speed =(0.1f);
        bigShooter.sightrange =(5);
        bigShooter.enemylevel =(3);

        EnemyTypeStats spectator = new EnemyTypeStats();
        enemytypelist.add(spectator);
        spectator.behaviour =(EnemyTypeStats.BehaviourType.SPECTATOR);
        spectator.shootAbility =(EnemyTypeStats.AbilityType.NONE);
        spectator.healthpoints =(10);
        spectator.picture =(16);
        spectator.speed =(0.1f);
        spectator.sightrange =(5);
        spectator.enemylevel =(1);

        EnemyTypeStats crab = new EnemyTypeStats();
        enemytypelist.add(crab);
        crab.behaviour =(EnemyTypeStats.BehaviourType.SHOOTER);
        crab.shootAbility =(EnemyTypeStats.AbilityType.FIREBULLET);
        crab.healthpoints =(12);
        crab.damage =(1);
        crab.picture =(40);
        crab.speed =(0.1f);
        crab.sightrange =(5);
        crab.enemylevel =(2);

        EnemyTypeStats kamikaze = new EnemyTypeStats();
        enemytypelist.add(kamikaze);
        kamikaze.behaviour =(EnemyTypeStats.BehaviourType.KAMIKAZE);
        kamikaze.shootAbility =(EnemyTypeStats.AbilityType.KAMIKAZE);
        kamikaze.healthpoints =(12);
        kamikaze.damage =(1);
        kamikaze.picture =(48);
        kamikaze.speed =(0.1f);
        kamikaze.sightrange =(5);
        kamikaze.enemylevel =(2);
        
        EnemyTypeStats lurker = new EnemyTypeStats();
        enemytypelist.add(lurker);
        lurker.behaviour =(EnemyTypeStats.BehaviourType.LURKER);
        lurker.shootAbility =(EnemyTypeStats.AbilityType.FIREBULLET);
        lurker.healthpoints =(12);
        lurker.damage =(1);
        lurker.picture =(56);
        lurker.speed =(0.1f);
        lurker.sightrange =(5);
        lurker.enemylevel =(2);



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
