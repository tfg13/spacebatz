package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Engine;
import java.util.LinkedList;

/**
 * Die Hauptklasse des Clients
 *
 * @author michael
 */
public class Client {
    
    /**
     * Das derzeit laufende Level.
     */
    public static Level currentLevel;
    /*
     * aktueller Tick, wird einmal von Server empfangen werden...
     */
    private static int gametick;
    /*
     * List für alle aktuellen Bullets
     */
    private static LinkedList<Bullet> BulletList = new LinkedList<Bullet>();
    
    // Einstiegspunkt:
    public static void main(String[] args) {
        //Neues Level erstellen:
        currentLevel = LevelGenerator.generateLevel(128, 128);
        new Engine().start();
    }

    /**
     * @return the gametick
     */
    public static int getGametick() {
        return gametick;
    }

    /**
     * @param aGametick the gametick to set
     */
    public static void incrementGametick() {
        gametick++;
    }

    /**
     * @return the BulletList
     */
    public static LinkedList<Bullet> getBulletList() {
        return BulletList;
    }
}
