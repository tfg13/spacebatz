package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.shared.Level;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Das Level des Servers erweitert das ClientLevel um einige Infos die nur der Server braucht
 *
 * @author michael
 */
public class ServerLevel extends Level {

    /**
     * Die Liste mit Spawnern
     */
    private ArrayList<EnemySpawner> enemySpawners;

    /**
     * Konstruktor
     */
    public ServerLevel(int xSize, int ySize) {
        super(xSize, ySize);
        enemySpawners = new ArrayList<>();
    }

    /**
     * Gibt die Liste mit Spawnern zurück
     *
     * @return die Liste der Spawner
     */
    public ArrayList<EnemySpawner> getEnemySpawners() {
        return enemySpawners;
    }
}
