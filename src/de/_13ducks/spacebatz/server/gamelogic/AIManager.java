/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * Berechnet das Verhalten der mobs.
     *
     * @param allList die Liste aller mobs deren Verhalten berechnet werden soll
     */
    public static void computeMobBehavior(Collection<Entity> allList) {
        for (Entity e : allList) {
            if (e instanceof Enemy) {
                Enemy mob = (Enemy) e;
                // Hat der Mob ein Ziel?
                if (mob.getMyTarget() == null) {
                    // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
                    for (Client client : Server.game.clients.values()) {
                        Player player = client.getPlayer();
                        if (mob.getSightrange() > Distance.getDistance(mob.getX(), mob.getY(), player.getX(), player.getY())) {
                            mob.setMyTarget(player);
                        }
                    }
                } else {
                    // wenn er eins hat schaut er ob es noch in reichweite ist:
                    if (mob.getSightrange() * 2 < Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                        mob.setMyTarget(null);
                        mob.stopMovement();
                    } else {
                        // wenn es in reichweite ist  hinbewegen:
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
}
