package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Enthält alle Daten eines Laufenden Speils
 *
 * @author michael
 */
public class Game {

    /**
     * Liste der verbundenen Clients
     */
    public HashMap<Integer, Client> clients;
    /**
     * Liste aller dynamischen Objekte (z.B. Spieler, Mobs, ...)
     */
    public ArrayList<Char> chars;
    /**
     * Das Level
     */
    private ServerLevel level;
    /**
     * Das Serialisierte Level
     */
    private byte[] serializedLevel;
    /**
     * Der Server-Gametick.
     */
    private int tick;
    /**
     * Die nächste netID.
     */
    private int nextNetID = 1;

    /**
     * Konstruktor
     */
    public Game() {
        clients = new HashMap<>();
        chars = new ArrayList<>();
        level = new ServerLevel();
        LevelGenerator.generateLevel(level);

        // Level serialisieren, damit es später schnell an Clients gesendet werden kann:
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(level);
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedLevel = bs.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Wird gerufen, wenn ein neuer Client verbunden wurde
     *
     * @param client der neue Client
     */
    public void clientJoined(Client client) {
        if (client.clientID != -1) {
            clients.put(client.clientID, client);
            Server.msgSender.sendSetClientID(client);
            Server.msgSender.sendLevel(client);
            Player player = new Player(10, 10, newNetID(), client);
            Server.msgSender.sendSetPlayer(client, player);
            Server.msgSender.sendStartGame(client);
        } else {
            System.out.println("WARNING: Client connected, but Server is full!");
        }
    }

    /**
     * Gibt die bytes des serialisierten Levels zurück
     *
     * @return die bytes des serialisierten levels
     */
    public byte[] getSerializedLevel() {
        return serializedLevel;
    }

    public int getTick() {
        return tick;
    }

    public void incrementTick() {
        tick++;
    }

    public synchronized int newNetID() {
        return nextNetID++;
    }

    public int newClientID() {
        Set<Integer> ids = clients.keySet();
        for (int i = 0; i < Settings.SERVER_MAXPLAYERS; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }
        return -1;
    }
}
