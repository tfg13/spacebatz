package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.network.ClientNetwork;
import de._13ducks.spacebatz.client.network.MessageInterpreter;
import de._13ducks.spacebatz.shared.Level;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

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
     * Aktuell gültiger Gametick.
     * Heißt frozen, weil er während der Grafikberechnung eingefroren wird.
     */
    public static int frozenGametick;
    /**
     * Interner (nur diese Klasse) Gametick, der niemals eingefroren wird.
     */
    private static int gametick;
    /**
     * Die clientID, die uns der Server zugewiesen hat.
     */
    private static int clientID;
    /**
     * Die Zuordnung von netID zu Char.
     */
    public static HashMap<Integer, Char> netIDMap;
    /**
     * List für alle aktuellen Bullets.
     */
    private static LinkedList<Bullet> BulletList = new LinkedList<>();
    /**
     * Die Logik-Tickrate.
     */
    public static int tickrate;
    /**
     * Der Thread, der die Ticks hochzählt.
     */
    private static Timer tickTimer;

    // Einstiegspunkt:
    public static void startClient(String ip) {

        msgInterpreter = new MessageInterpreter();
        netIDMap = new HashMap<>();
        network = new ClientNetwork();
        if (getNetwork().tryConnect(ip)) {
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

    /**
     * Schickt ein vollständiges, gültiges Paket an den Server.
     *
     * @param packet udp-Paket
     */
    public static void udpOut(byte[] packet) {
        if (packet.length != Settings.NET_UDP_CTS_SIZE || packet[0] != clientID) {
            throw new IllegalArgumentException("Illegal packet!");
        }
        getNetwork().udpSend(packet);
    }

    /**
     * Leitet UDP-Ticks ans Netzwerksystem weiter.
     */
    public static void udpTick() {
        getNetwork().udpTick();
    }

    /**
     * Gibt das Netzwerkmodul zurück
     * @return das Netzwerkmodul des Clients
     */
    public static ClientNetwork getNetwork() {
        return network;
    }
    
    public static void updateGametick() {
        frozenGametick = gametick;
    }

    public static void startTickCounting(int serverStartTick) {
        gametick = serverStartTick - (Settings.NET_TICKSYNC_MAXPING / (1000 / tickrate));
        tickTimer = new Timer(true);
        tickTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                gametick++;
            }
        }, 0, 1000 / tickrate);
    }
}
