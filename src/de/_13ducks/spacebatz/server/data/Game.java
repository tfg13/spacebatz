package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Enthält alle Daten eines Laufenden Speils
 * @author michael
 */
public class Game {

    /**
     * Liste der verbundenen Clients
     */
    public ArrayList<Client> clients;
    /**
     * Liste aller dynamischen Objekte
     * (z.B. Spieler, Mobs, ...)
     */
    public ArrayList<Char> chars;
    /**
     * Das Level
     */
    private ServerLevel level;
    /**
     * Das Serialisierte Level
     */
    private byte[] serializedLevelMessage;
    
    /**
     * Der Server-Gametick.
     */
    private int tick;

    /**
     * Konstruktor
     */
    public Game() {
        clients = new ArrayList<>();
        chars = new ArrayList<>();
        level = new ServerLevel();

        // Level serialisieren, damit es später schnell an Clients gesendet werden kann:
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(level);
            serializedLevelMessage = new byte[bs.toByteArray().length + 2];
            for (int i = 0; i < bs.toByteArray().length; i++) {
                serializedLevelMessage[2 + i] = bs.toByteArray()[i];
            }
            serializedLevelMessage[0] = (byte) serializedLevelMessage.length;
            serializedLevelMessage[1] = (byte) 20;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Wird gerufen, wenn ein neuer Client verbunden wurde
     * @param client der neue Client
     */
    public void clientJoined(Client client) {
        clients.add(client);
        Server.msgSender.sendLevel(client);
    }

    /**
     * Gibt die bytes des serialisierten Levels zurück
     * @return die bytes des serialisierten levels
     */
    public byte[] getSerializedLevel() {
        return serializedLevelMessage;
    }
    
    public int getTick() {
        return tick;
    }
    
    public void incrementTick() {
        tick++;
    }
}
