/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.shared;

/**
 * Beschreibt ein Level.
 *
 * Derzeit nicht mehr als ein großes Zahlenarray mit den Bodentexturen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Level implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Die Bodentexturen. Indizes der Tiles
     */
    public int[][] ground;
    /**
     * Die Wandtexturen.
     */
    public int[][] top;
    /**
     * Die Beleuchtungsmap.
     */
    public byte[][] shadow;
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
        if (xSize % 8 != 0 || ySize % 8 != 0) {
            throw new IllegalArgumentException("Level size must be a multiple of 8");
        }
        this.sizeX = xSize;
        this.sizeY = ySize;
        ground = new int[xSize][ySize];
        top = new int[xSize][ySize];
        collisionMap = new boolean[xSize][ySize];
        shadow = new byte[xSize][ySize];
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

    /**
     * Gibt die Breite des Levels (in Tiles) zurück.
     *
     * @return die Breite des Levels
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Gibt die Höhe des Levels (in Tiles) zurück.
     *
     * @return die Höhe des Levels
     */
    public int getSizeY() {
        return sizeY;
    }
}
