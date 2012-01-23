package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.server.data.Wall;
import java.util.ArrayList;
import java.util.Random;

/**
 * Beschreibt ein Level.
 *
 * Derzeit nicht mehr als ein großes Zahlenarray mit den Bodentexturen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Level implements java.io.Serializable {

    /**
     * Liste der Blockierten Flächen im Level
     */
    private ArrayList<Wall> walls;
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
        ground = new int[xSize][ySize];
        walls = new ArrayList<>();
    }

    public int[][] getGround() {
        return ground;
    }

    public void setGround(int[][] ground) {
        this.ground = ground;
    }

    /**
     * Fügt eine neue Kollisionsfläche hinzu
     * @param collision die neue Kollisionsfläche
     */
    public void addWall(Wall collision) {
        walls.add(collision);
    }
}
