package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Bits;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Enthält alle Daten eines Laufenden Spiels
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
     * Liste aller Geschosse
     */
    public ArrayList<Bullet> bullets;
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
        bullets = new ArrayList<>();
        LevelGenerator.generateLevel(level);
        Enemy testenemy = new Enemy(1, 2, newNetID());
        chars.add(testenemy);

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
            Server.serverNetwork.udp.addClient(client, (byte) client.clientID);
            Server.msgSender.sendSetClientID(client);
            Server.msgSender.sendLevel(client);
            Player player = new Player(10, 10, newNetID(), client);
            Server.msgSender.sendSetPlayer(client, player);
            chars.add(player);
            Server.msgSender.sendStartGame(client);
            Server.msgSender.sendNewPlayer(client);
            // Diesem Client alle anderen (alten) Chars schicken:
            Server.msgSender.sendAllChars(client);
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

        // Ab hier: Testcode, spawnt Bullets
        if (tick % 30 == 0) {
            Random random = new Random(System.nanoTime());
            Bullet bullet = new Bullet(tick, 7.0, 7.0,  random.nextGaussian() * Math.PI / 16, 0.15f, newNetID());
            bullets.add(bullet);
            byte[] bytearray = new byte[25];
            
            bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
            Bits.putInt(bytearray, 1, bullet.getSpawntick());
            Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
            Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
            Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
            Bits.putFloat(bytearray, 17, (float) bullet.getSpeed());
            Bits.putInt(bytearray, 21, bullet.getNetID());
            
            for (int i = 0; i < clients.size(); i++) {
                Server.serverNetwork.udp.sendPack(bytearray, clients.get(i));
            }
        }
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
