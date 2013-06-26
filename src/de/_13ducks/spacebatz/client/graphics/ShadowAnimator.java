package de._13ducks.spacebatz.client.graphics;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.util.geo.IntVector;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Verwaltet Schatten und Schattenanimationen. Neu ankommende Schattenänderungen werden hier zwischengecached und nach und nach angewendet. Dadurch sieht es so aus, als würden sich
 * Schatten kontinuierlich aufdecken.
 *
 * Dazu wird eine Liste von Schatten-Chunks verwaltet, die bei den Ticks stückweise in die echten Daten kopiert wird.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ShadowAnimator {

    /**
     * Speichert die derzeit animierten Chunks.
     */
    private HashMap<IntVector, byte[][]> shadowChange = new HashMap<>();

    /**
     * Muss einmal pro Client-Tick aufgerufen werden, verwaltet Schattendaten.
     */
    public void tick() {
        if (GameClient.currentLevel == null) {
            // Level noch nicht geladen, abbruch
            return;
        }
        byte[][] gameShadows = GameClient.currentLevel.shadow;
        // Alle durchgehen und die Game-Werte in jedem Tick annähern, bis alle erreicht sind.
        // Dann die Animation löschen
        Iterator<IntVector> iter = shadowChange.keySet().iterator();
        while (iter.hasNext()) {
            IntVector chunkCoords = iter.next();
            byte[][] chunkData = shadowChange.get(chunkCoords);
            boolean somethingChanged = false;
            for (int x = 0; x < 8; x++) {
                int realX = chunkCoords.x * 8 + x;
                for (int y = 0; y < 8; y++) {
                    int realY = chunkCoords.y * 8 + y;
                    int toGo = gameShadows[realX][realY] - chunkData[x][y];
                    if (toGo > 0) {
                        // 2 auf einmal - falls das noch möglich ist.
                        gameShadows[realX][realY] -= Math.min(toGo, 2);
                        somethingChanged = true;
                    }
                }
            }
            // Animation zu Ende?
            if (!somethingChanged) {
                iter.remove();
            }
        }
    }

    /**
     * Aufrufen, um ein Chunk zur Animation einzufügen. Sollte dieser Chunk bereits animiert werden, werden die Animationen automatisch gemerged.
     *
     * @param chunkX X-Koordinate des Chunks
     * @param chunkY Y-Koordinate des Chunks
     * @param chunkData Beleuchtungsdaten
     */
    public void receivedChunk(int chunkX, int chunkY, byte[][] chunkData) {
        IntVector chunkCoords = new IntVector(chunkX, chunkY);
        if (shadowChange.containsKey(chunkCoords)) {
            // Es gibt schon eine Animation, beide mergen.
            byte[][] oldChunkData = shadowChange.get(chunkCoords);
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    // Immer das hellere nehmen
                    oldChunkData[x][y] = (byte) Math.min(oldChunkData[x][y], chunkData[x][y]);
                }
            }
        } else {
            // Nur einfügen, es ist noch keine Animation da.
            shadowChange.put(chunkCoords, chunkData);
        }
    }
}
