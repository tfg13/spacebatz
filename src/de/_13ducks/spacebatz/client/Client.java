package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.client.graphics.Engine;
import java.util.ArrayList;
import de._13ducks.spacebatz.client.network.ClientNetwork;
import de._13ducks.spacebatz.client.network.MessageInterpreter;
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
    /**
     * Der NAchrichteninterpreter
     */
    private static MessageInterpreter msgInterpreter;
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
    public static void startClient(String ip) {

        msgInterpreter = new MessageInterpreter();
        network = new ClientNetwork();
        if (network.tryConnect(ip)) {
            //Neues Level erstellen:
            player = new Player(30, 30);
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

    /**
     * Getter für den MessageInterpreter
     * @return der MessageInterpreter
     */
    public static MessageInterpreter getMsgInterpreter() {
        return msgInterpreter;
    }
}
