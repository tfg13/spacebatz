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

        EnemyTypeStats smallShooter = new EnemyTypeStats();
        enemytypelist.add(smallShooter);
        smallShooter.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        smallShooter.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        smallShooter.abilityAttackspeed = 3;
        smallShooter.abilityBulletpic = 1;
        smallShooter.abilityBulletspeed = 0.15;
        smallShooter.abilityDamage = 5;
        smallShooter.abilityDamagespread = 0;
        smallShooter.abilityExplosionradius = 0;
        smallShooter.abilityMaxoverheat = 10;
        smallShooter.abilityReduceoverheat = 1;
        smallShooter.abilityRange = 10;
        smallShooter.abilitySpread = 0;
        smallShooter.healthpoints = (30);
        smallShooter.damage = (5);
        smallShooter.picture = (0);
        smallShooter.speed = (0.1f);
        smallShooter.sightrange = (5);
        smallShooter.enemylevel = (2);

        EnemyTypeStats bigShooter = new EnemyTypeStats();
        enemytypelist.add(bigShooter);
        bigShooter.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        bigShooter.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        bigShooter.abilityAttackspeed = 1;
        bigShooter.abilityBulletpic = 1;
        bigShooter.abilityBulletspeed = 0.15;
        bigShooter.abilityDamage = 15;
        bigShooter.abilityDamagespread = 0;
        bigShooter.abilityExplosionradius = 1;
        bigShooter.abilityMaxoverheat = 10;
        bigShooter.abilityReduceoverheat = 1;
        bigShooter.abilityRange = 10;
        bigShooter.abilitySpread = 0;
        bigShooter.healthpoints = (40);
        bigShooter.damage = (15);
        bigShooter.picture = (24);
        bigShooter.speed = (0.1f);
        bigShooter.sightrange = (5);
        bigShooter.enemylevel = (3);

        EnemyTypeStats spectator = new EnemyTypeStats();
        enemytypelist.add(spectator);
        spectator.behaviour = (EnemyTypeStats.BehaviourType.SPECTATOR);
        spectator.shootAbility = (EnemyTypeStats.AbilityType.NONE);
        spectator.healthpoints = (10);
        spectator.picture = (16);
        spectator.speed = (0.1f);
        spectator.sightrange = (5);
        spectator.enemylevel = (1);

        EnemyTypeStats crab = new EnemyTypeStats();
        enemytypelist.add(crab);
        crab.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        crab.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        smallShooter.abilityAttackspeed = 1;
        crab.abilityBulletpic = 1;
        crab.abilityBulletspeed = 0.15;
        crab.abilityDamage = 10;
        crab.abilityDamagespread = 0;
        crab.abilityExplosionradius = 0;
        crab.abilityMaxoverheat = 10;
        crab.abilityReduceoverheat = 1;
        crab.abilityRange = 10;
        crab.abilitySpread = 0;
        crab.healthpoints = (12);
        crab.damage = (10);
        crab.picture = (40);
        crab.speed = (0.1f);
        crab.sightrange = (5);
        crab.enemylevel = (2);
        
        
                EnemyTypeStats drake = new EnemyTypeStats();
        enemytypelist.add(drake);
        drake.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        drake.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        smallShooter.abilityAttackspeed = 1;
        drake.abilityBulletpic = 1;
        drake.abilityBulletspeed = 0.15;
        drake.abilityDamage = 30;
        drake.abilityDamagespread = 0;
        drake.abilityExplosionradius = 10;
        drake.abilityMaxoverheat = 10;
        drake.abilityReduceoverheat = 1;
        drake.abilityRange = 10;
        drake.abilitySpread = 0;
        drake.healthpoints = (12);
        drake.damage = (1);
        drake.picture = (28);
        drake.speed = (0.1f);
        drake.sightrange = (5);
        drake.enemylevel = (2);

        EnemyTypeStats kamikaze = new EnemyTypeStats();
        enemytypelist.add(kamikaze);
        kamikaze.behaviour = (EnemyTypeStats.BehaviourType.KAMIKAZE);
        kamikaze.shootAbility = (EnemyTypeStats.AbilityType.KAMIKAZE);
        kamikaze.abilityDamage = 75;
        kamikaze.abilityRange = 6;
        kamikaze.healthpoints = (15);
        kamikaze.damage = (60);
        kamikaze.picture = (48);
        kamikaze.speed = (0.1f);
        kamikaze.sightrange = (5);
        kamikaze.enemylevel = (2);

        EnemyTypeStats lurker = new EnemyTypeStats();
        enemytypelist.add(lurker);
        lurker.behaviour = (EnemyTypeStats.BehaviourType.LURKER);
        lurker.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        lurker.abilityAttackspeed = 1.0;
        lurker.abilityBulletpic = 1;
        lurker.abilityBulletspeed = 0.25;
        lurker.abilityDamage = 40;
        lurker.abilityDamagespread = 0;
        lurker.abilityExplosionradius = 0;
        lurker.abilityMaxoverheat = 3;
        lurker.abilityReduceoverheat = 0.01;
        lurker.abilityRange = 10;
        lurker.abilitySpread = 0;
        lurker.healthpoints = (30);
        lurker.damage = (40);
        lurker.picture = (56);
        lurker.speed = (0.1f);
        lurker.sightrange = (5);
        lurker.enemylevel = (2);

        EnemyTypeStats boss = new EnemyTypeStats();
        enemytypelist.add(boss);
        boss.behaviour = (EnemyTypeStats.BehaviourType.SHOOTER);
        boss.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        boss.abilityAttackspeed = 1;
        boss.abilityBulletpic = 2;
        boss.abilityBulletspeed = 0.15;
        boss.abilityDamage = 50;
        boss.abilityDamagespread = 0;
        boss.abilityExplosionradius = 0;
        boss.abilityMaxoverheat = 10;
        boss.abilityReduceoverheat = 1;
        boss.abilityRange = 20;
        boss.abilitySpread = 0.15;
        boss.healthpoints = (200);
        boss.damage = (1);
        boss.picture = (32);
        boss.speed = (0.2f);
        boss.sightrange = (10);
        boss.enemylevel = (5);

        EnemyTypeStats kiter = new EnemyTypeStats();
        enemytypelist.add(kiter);
        kiter.behaviour = (EnemyTypeStats.BehaviourType.KITER);
        kiter.shootAbility = (EnemyTypeStats.AbilityType.FIREBULLET);
        kiter.abilityAttackspeed = 1;
        kiter.abilityBulletpic = 2;
        kiter.abilityBulletspeed = 0.15;
        kiter.abilityDamage = 20;
        kiter.abilityDamagespread = 0;
        kiter.abilityExplosionradius = 0;
        kiter.abilityMaxoverheat = 10;
        kiter.abilityReduceoverheat = 1;
        kiter.abilityRange = 20;
        kiter.abilitySpread = 0.15;
        kiter.healthpoints = (15);
        kiter.damage = (20);
        kiter.picture = (12);
        kiter.speed = (0.16f);
        kiter.sightrange = (10);
        kiter.enemylevel = (5);

        EnemyTypeStats summoner = new EnemyTypeStats();
        enemytypelist.add(summoner);
        summoner.behaviour = (EnemyTypeStats.BehaviourType.SUMMONER);
        summoner.shootAbility = (EnemyTypeStats.AbilityType.SUMMONENEMYS);
        summoner.healthpoints = (5);
        summoner.picture = (20);
        summoner.speed = (0.1f);
        summoner.sightrange = (10);
        summoner.enemylevel = (5);

    }

    /**
     * @return the enemytypelist
     */
    public ArrayList<EnemyTypeStats> getEnemytypelist() {
        return enemytypelist;
    }
}
