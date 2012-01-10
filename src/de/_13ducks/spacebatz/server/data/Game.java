package de._13ducks.spacebatz.server.data;

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
    private byte[] serializedLevel;

    /**
     * Konstruktor
     */
    public Game() {
        clients = new ArrayList<Client>();
        chars = new ArrayList<>();
        level = new ServerLevel();

        // Level serialisieren, damit es später schnell an Clients gesendet werden kann:
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(level);

            serializedLevel = bs.toByteArray();
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
    }

    /**
     * Gibt die bytes des serialisierten Levels zurück
     * @return die bytes des serialisierten levels
     */
    public byte[] getSerializedLevel() {
        return serializedLevel;
    }
}
