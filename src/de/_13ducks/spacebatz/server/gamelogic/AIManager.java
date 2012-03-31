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
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Enemy;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.server.data.Player;
import de._13ducks.spacebatz.util.Distance;
import java.util.Collection;

/**
 * Berechnet das Verhalten der Mobs
 *
 * @author michael
 */
public class AIManager {

    /**
     * Konstante für für Enemys die nichts machen
     */
    public static final int AITYPE_NONE = 0;
    /**
     * Konstante für Standard Mobverhalten (Bei Sichtkontakt zum Spieler hinrennen und ihn verfolgen)
     */
    public static final int AITYPE_STANDARD = 1;

    /**
     * Berechnet das Verhalten der mobs.
     *
     * @param allList die Liste aller mobs deren Verhalten berechnet werden soll
     */
    public static void computeMobBehavior(Collection<Entity> allList) {
        for (Entity e : allList) {
            if (e instanceof Enemy) {
                Enemy mob = (Enemy) e;

                switch (mob.getAiType()) {
                    case AITYPE_NONE:
                        break;
                    case AITYPE_STANDARD:
                        computeStandardMobBehaviour(mob);
                        break;
                }


            }
        }
    }

    /**
     * Berechnet das Standard-MobverhaltenX
     *
     * @param mob der Enemy für den das Standardverhalten berechnet werde nsoll
     */
    private static void computeStandardMobBehaviour(Enemy mob) {

        // Hat der Mob ein Ziel?
        if (mob.getMyTarget() == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (mob.getProperty("sightrange") > Distance.getDistance(mob.getX(), mob.getY(), player.getX(), player.getY())) {
                    mob.setMyTarget(player);
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (mob.getProperty("sightrange") * 2 < Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                mob.setMyTarget(null);
                mob.stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (1.0 > Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                    mob.stopMovement();
                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vecX = mob.getMyTarget().getX() - mob.getX();
                    double vecY = mob.getMyTarget().getY() - mob.getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vecX == 0.0) {
                        vecX = 0.1;
                    }
                    if (vecY == 0.0) {
                        vecY = 0.1;
                    }
                    mob.setVector(vecX, vecY);
                }



            }
        }

    }
}
