package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Enemy;
import de._13ducks.spacebatz.server.data.EnemySpawner;
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
        for (EnemySpawner spawner : Server.game.getLevel().getEnemySpawners()) {
            spawner.tick();
        }

    }
}
