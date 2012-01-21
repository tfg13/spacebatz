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
                    for (int j = 0; j < Server.game.chars.size(); j++) {
                        Char theChar = Server.game.chars.get(j);
                        if (Settings.MOB_AGGRO_RANGE < Distance.getDistance(mob.getX(), mob.getY(), theChar.getX(), theChar.getY())) {
                            mob.setMyTarget(theChar);
                        }
                    }
                } else {
                    
                    mob.setVector(mob.getMyTarget().posX - mob.getX(), mob.getMyTarget().posY - mob.getY());
                    
                }
            }


        }
    }
}
