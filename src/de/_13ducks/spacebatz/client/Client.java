package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.network.ClientNetwork;
import de._13ducks.spacebatz.client.network.MessageInterpreter;
import de._13ducks.spacebatz.shared.Level;
import java.util.HashMap;
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
     * Der Nachrichteninterpreter.
     */
    private static MessageInterpreter msgInterpreter;
    /**
     * Der Spieler.
     */
    public static Player player;
    /**
     * aktueller Tick, wird einmal von Server empfangen werden...
     */
    public static int gametick;
    /**
     * Die clientID, die uns der Server zugewiesen hat.
     */
    private static int clientID;
    /**
     * Die Zuordnung von netID zu Char.
     */
    public static HashMap<Integer, Char> netIDMap;
    ;
    /*
     * List für alle aktuellen Bullets
     */
    private static LinkedList<Bullet> BulletList = new LinkedList<>();

    // Einstiegspunkt:
    public static void startClient(String ip) {

        msgInterpreter = new MessageInterpreter();
        netIDMap = new HashMap<>();
        network = new ClientNetwork();
        if (network.tryConnect(ip)) {
            // StartRequest per TCP an Server schicken
            //player = new Player(30, 30);
        } else {
            System.out.println("ERROR: Can't connect!");
        }
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
     *
     * @return der MessageInterpreter
     */
    public static MessageInterpreter getMsgInterpreter() {
        return msgInterpreter;
    }

    /**
     * @return the clientID
     */
    public static int getClientID() {
        return clientID;
    }

    /**
     * @param clientID the clientID to set
     */
    public static void setClientID(int clientID) {
        Client.clientID = clientID;
    }
}
