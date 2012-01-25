/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Distance;
import java.util.ArrayList;

/**
 * Berechnet das Verhalten der Mobs
 * @author michael
 */
public class AIManager {

    /**
     * Berechnet das Verhalten der mobs.
     * 
     * @param mobs die Liste aller mobs deren Verhalten berechnet werden soll 
     */
    public static void computeMobBehavior(ArrayList<Char> mobs) {
        for (int i = 0; i < mobs.size(); i++) {
            if (mobs.get(i) instanceof Enemy) {
                Enemy mob = (Enemy) mobs.get(i);

                // Hat der Mob ein Ziel?
                if (mob.getMyTarget() == null) {
                    // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
                    for (int j = 0; j < Server.game.chars.size(); j++) {
                        if (Server.game.chars.get(j) instanceof Player) {
                            Char theChar = Server.game.chars.get(j);
                            if (mob.getSightrange() > Distance.getDistance(mob.getX(), mob.getY(), theChar.getX(), theChar.getY())) {
                                mob.setMyTarget(theChar);
                            }
                        }
                    }
                } else {
                    // wenn er eins hat schaut er ob es noch in reichweite ist:
                    if (mob.getSightrange() * 2 < Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                        mob.setMyTarget(null);
                        mob.stopMovement();
                    } else {
                        // wenn es in reichweite ist  hinbewegen:
                        double vecX = mob.getMyTarget().posX - mob.getX();
                        double vecY = mob.getMyTarget().posY - mob.getY();

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
