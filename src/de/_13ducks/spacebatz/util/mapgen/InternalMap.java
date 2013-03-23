package de._13ducks.spacebatz.util.mapgen;

import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.util.geo.PolyMesh;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Die interne Darstellung einer Map, eine Kombination aus Polyonnetzen und
 * Raster.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class InternalMap {

    public PolyMesh polygons;
    public HashMap<String, Object> metadata = new HashMap<>();
    public int[][] groundTex;
    public int[][] topTex;
    public byte[][] ground_randomize;
    public byte[][] top_randomize;
    public boolean[][] collision;
    public byte[][] shadow;
    public HashMap<Integer, Entity> startEntitys = new HashMap<>();
    public ArrayList<Quest> quests = new ArrayList<>();
    public String hash;

    /**
     * Wandelt die interne Map-Darstellung in ein ServerLevel um.
     */
    public ServerLevel toServerLevel() {
        ServerLevel level = new ServerLevel(groundTex.length, groundTex[0].length, hash, quests, startEntitys);
        level.ground = groundTex;
        level.top = topTex;
        level.ground_randomize = ground_randomize;
        level.top_randomize = top_randomize;
        for (int x = 0; x < groundTex.length; x++) {
            System.arraycopy(collision[x], 0, level.getCollisionMap()[x], 0, groundTex[0].length);
        }
        level.shadow = shadow;
        if (level.shadow == null) {
            // Alles auf 0, also kein Schatten = sichtbar
            level.shadow = new byte[groundTex.length][groundTex[0].length];
        }
        // Spawn steht in metadaten
        int[] spawn = (int[]) metadata.get("SPAWN");
        level.respawnX = spawn[0];
        level.respawnY = spawn[1];
        return level;
    }
}
