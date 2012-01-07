package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Engine;

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

    // Einstiegspunkt:
    public static void main(String[] args) {
        currentLevel = new Level(64, 64);
        new Engine().start();
    }
}
