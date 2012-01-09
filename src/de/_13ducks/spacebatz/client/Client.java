package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.client.network.ClientNetwork;

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
    /**
     * Das Netzwerksystem.
     */
    private static ClientNetwork network;

    // Einstiegspunkt:
    public static void main(String[] args) {
        String targetIp = "localhost"; // Das ist default
        for (String s : args) {
            if (s.startsWith("ip=")) {
                targetIp = s.substring(s.indexOf("=") + 1);
            }
        }
        network = new ClientNetwork();
        if (network.tryConnect(targetIp)) {
            //Neues Level erstellen:
            currentLevel = LevelGenerator.generateLevel(128, 128);
            new Engine().start();
        } else {
            System.out.println("ERROR: Can't connect!");
        }
    }
}