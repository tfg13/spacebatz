package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Plant;

/**
 * Verwaltet Pflanzen und deren Wachstum
 *
 * @author michael
 */
public class VegetationManager {

    /**
     * Berechnet Wachstum der Pflanzen
     */
    public static void calculateVegetationGrowth() {
        for (int i = 0; i < Server.game.getPlants().size(); i++) {
            Plant p = Server.game.getPlants().get(i);
            if (Math.random() < 0.0001) {
                int dx = -1 + (int) (Math.random() * 2);
                int dy = -1 + (int) (Math.random() * 2);
                Server.game.getPlants().add(new Plant(p.getX() + dx, p.getY() + dy));
            }
        }
    }
}
