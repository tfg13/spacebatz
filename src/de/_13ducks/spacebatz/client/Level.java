package de._13ducks.spacebatz.client;

import java.util.Random;

/**
 * Beschreibt ein Level.
 *
 * Derzeit nicht mehr als ein großes Zahlenarray mit den Bodentexturen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Level {

    /**
     * Die Bodentexturen. Indizes der Tiles
     */
    private int[][] ground;

    /**
     * Erzeugt eine neues Level.
     *
     * @param xSize Größe in X-Richtung
     * @param ySize Größe in Y-Richtung
     */
    public Level(int xSize, int ySize) {
        ground = new int[xSize][ySize];

        // Default-Bodentextur:
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                ground[x][y] = 1;
            }
        }
        // Ein Paar Krater zufällig verteilen
        Random random = new Random(System.nanoTime());
        for (int i = 0; i < 50; i++) {
            ground[random.nextInt(xSize)][random.nextInt(ySize)] = 2;
        }
    }
    
    public int[][] getGround() {
        return ground;
    }
}
