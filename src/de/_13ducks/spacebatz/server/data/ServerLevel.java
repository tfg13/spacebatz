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
     * Die Liste mit Gebieten, in denen Gegner gespawnt werden
     */
    transient private ArrayList<EnemySpawnArea> enemySpawnAreas;

    /**
     * Konstruktor
     */
    public ServerLevel(int xSize, int ySize) {
        super(xSize, ySize);
        enemySpawnAreas = new ArrayList<>();
    }

    /**
     * Gibt die Liste mit Spawnern zurück
     *
     * @return die Liste der Spawner
     */
    public ArrayList<EnemySpawnArea> getEnemySpawners() {
        return enemySpawnAreas;
    }

    /**
     * Fügt der Liste ein neues Spawngebiet hinzu
     */
    public void addEnemySpawnArea(EnemySpawnArea spawnArea) {
        enemySpawnAreas.add(spawnArea);
    }
}
