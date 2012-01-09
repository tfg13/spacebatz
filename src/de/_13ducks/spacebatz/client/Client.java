package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Engine;
import java.util.ArrayList;
import de._13ducks.spacebatz.client.network.ClientNetwork;
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
    /**
     * Das Netzwerksystem.
     */
    private static ClientNetwork network;
    /*
     * Der Spieler
     */
    private static Player player;
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
			player = new Player(30, 30);
            new Engine().start();
        } else {
            System.out.println("ERROR: Can't connect!");
        }
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

    /**
     * @return the player
     */
    public static Player getPlayer() {
        return player;
    }
}
