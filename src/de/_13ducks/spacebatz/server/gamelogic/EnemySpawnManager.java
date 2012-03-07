package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Enemy;
import de._13ducks.spacebatz.server.data.Player;
import java.util.Random;

/**
 * Verwaltet das spawnen von Gegnern in der Nähe der Spieler
 *
 * @author michael
 */
public class EnemySpawnManager {

    /**
     * Spawn Gegner in der Nähe der Spieler, wenn Die Gegnerdicht nicht zu hoch ist
     */
    public static void spawnEnemys() {
        for (Client c : Server.game.clients.values()) {
            Player player = c.getPlayer();
            double x1 = (player.getX() - Settings.SERVER_ENEMYSPAWNMAXDISTANCE);
            double y1 = (player.getX() - Settings.SERVER_ENEMYSPAWNMAXDISTANCE);
            double x2 = (player.getX() + Settings.SERVER_ENEMYSPAWNMAXDISTANCE);
            double y2 = (player.getX() + Settings.SERVER_ENEMYSPAWNMAXDISTANCE);
            if (Server.entityMap.getEntitiesInArea((int) x1, (int) y1, (int) x2, (int) y2).size() < Settings.SERVER_ENEMYSPAWNDENSITY) {
            }
        }
    }
}
