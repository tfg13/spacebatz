package de._13ducks.spacebatz.shared;

/**
 * Beschreibt ein Level.
 *
 * Derzeit nicht mehr als ein großes Zahlenarray mit den Bodentexturen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Level implements java.io.Serializable {

    /**
     * Die Bodentexturen. Indizes der Tiles
     */
    private int[][] ground;
    /**
     * Kollisionskarte, true = kollision, false = frei
     */
    private boolean collisionMap[][];
    private int sizeX;
    private int sizeY;
    /**
     * Die Respawn-Position im Level
     */
    public int respawnX, respawnY;

    /*
     * Konstruktor, wird vom Levelgenerator augerufen
     */
    public Level(int xSize, int ySize) {
        this.sizeX = xSize;
        this.sizeY = ySize;
        ground = new int[xSize][ySize];
        collisionMap = new boolean[xSize][ySize];
    }

    public int[][] getGround() {
        return ground;
    }

    public void setGround(int[][] ground) {
        this.ground = ground;
    }

    /**
     * Gibt die Kollisionskarte zurück
     *
     * @return die Kollisionskarte
     */
    public boolean[][] getCollisionMap() {
        return collisionMap;
    }

    /**
     * Ändert den Wert einer eines einzelnen Feldes
     *
     * @param x X-Koordinate des Feldes
     * @param y Y-Koordinate des Feldes
     * @param value der neue Wert des Feldes
     */
    public void setSingleTile(int x, int y, int value) {
        this.ground[x][y] = value;
    }
}
