package de._13ducks.spacebatz.shared;

import java.util.Random;

/**
 * Beschreibt ein Level.
 *
 * Derzeit nicht mehr als ein großes Zahlenarray mit den Bodentexturen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Level implements java.io.Serializable{

    /**
     * Die Bodentexturen. Indizes der Tiles
     */
    private int[][] ground;
    
    private int sizeX;
    private int sizeY;

    /*
     * Konstruktor, wird vom Levelgenerator augerufen
     */
    public Level(int xSize, int ySize) {
        this.sizeX = xSize;
        this.sizeY = ySize;
    }
    
    public int[][] getGround() {
        return ground;
    }

    public void setGround(int[][] ground) {
        this.ground = ground;
    }
}
