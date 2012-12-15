package de._13ducks.spacebatz.util.mapgen;

import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.util.mapgen.data.PolyMesh;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Die interne Darstellung einer Map, eine Kombination aus Polyonnetzen und Raster.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class InternalMap {

    public PolyMesh polygons;
    public HashMap<String, Object> metadata = new HashMap<>();
    public int[][] groundTex;
    public boolean[][] collision;
    public ArrayList<Quest> quests = new ArrayList<>();

    /**
     * Wandelt die interne Map-Darstellung in ein ServerLevel um.
     */
    public ServerLevel toServerLevel() {
        ServerLevel level = new ServerLevel(groundTex.length, groundTex[0].length, quests);
        level.setGround(groundTex);
        for (int x = 0; x < groundTex.length; x++) {
            System.arraycopy(collision[x], 0, level.getCollisionMap()[x], 0, groundTex[0].length);
        }
        // Spawn steht in metadaten
        int[] spawn = (int[]) metadata.get("SPAWN");
        level.respawnX = spawn[0];
        level.respawnY = spawn[1];
        return level;
    }
}
