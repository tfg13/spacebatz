package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.EnemyTypes;
import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.Distance;
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
     * Liste aller Gegnertypen
     */
    public EnemyTypes enemytypes;
    /**
     * Das Level
     */
    private ServerLevel level;
    /**
     * Das Serialisierte Level
     */
    private byte[] serializedLevel;
    /**
     * Die serialistierten enemytypes
     */
    private byte[] serializedEnemyTypes;
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
        enemytypes = new EnemyTypes();
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

        // Enemytypes serialisieren, damit es später schnell an Clients gesendet werden kann:
        ByteArrayOutputStream bs2 = new ByteArrayOutputStream();
        ObjectOutputStream os2;
        try {
            os2 = new ObjectOutputStream(bs2);
            os2.writeObject(enemytypes);
            os2.flush();
            bs2.flush();
            bs2.close();
            os2.close();
            serializedEnemyTypes = bs2.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addEnemies() {
        Enemy testenemy = new Enemy(3, 55, newNetID(), 0);
        chars.add(testenemy);

        // Platziert Gegner
        Random r = new Random();
        for (int i = 0; i < 90; i++) {
            double posX = r.nextDouble() * level.getGround().length;
            double posY = r.nextDouble() * level.getGround().length;


            if (10.0 < Distance.getDistance(posX, posY, 3, 3)) {
                int enemytype = r.nextInt(30);
                if (enemytype > 1) {
                    enemytype = 1;
                }
                chars.add(new Enemy(posX, posY, newNetID(), enemytype));
            }

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
            Server.msgSender.sendEnemyTypes(client);
            Player player = new Player(level.respawnX, level.respawnY, newNetID(), client);
            Server.msgSender.sendSetPlayer(client, player);
            chars.add(player);
            // Dem Client die Tickrate schicken:
            Server.msgSender.sendTickrate(client);
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

    /**
     * Gibt die bytes des serialisierten enemytypes zurück
     *
     * @return die bytes des serialisierten enemytypes
     */
    public byte[] getSerializedEnemyTypes() {
        return serializedEnemyTypes;
    }

    public int getTick() {
        return tick;
    }

    /**
     * Berechnet die GameLogic für einen Tick.
     */
    public void gameTick() {
        // KI berechnen:
        AIManager.computeMobBehavior(this.chars);
        // Kollision berechnen:
        CollisionManager.computeCollision();



    }

    /**
     * Inkrementiert den GameTick
     */
    public void incrementTick() {
        tick++;
    }

    /**
     * Gibt eine neue netID, die noch frei ist, zurück
     * @return eine neue netID
     */
    public final synchronized int newNetID() {
        return nextNetID++;
    }

    public final int newClientID() {
        Set<Integer> ids = clients.keySet();
        for (int i = 0; i < Settings.SERVER_MAXPLAYERS; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Erzeugt ein Bullet und schickt es an die Clients
     * 
     * @param posX die X-Koordinate an der das Bulelt erstellt wird
     * @param posY die Y-Koordinate an der das Bulelt erstellt wird
     * @param direction die Richtung, in die das Bullet fliegt
     * @param char Der Char, der es abgefeuert hat
     */
    public void fireBullet(double posX, double posY, double direction, Char c) {
        Random random = new Random(System.nanoTime());

        Bullet bullet = new Bullet(this.getTick(), posX, posY, direction + random.nextGaussian() * Math.PI / 64 * 0, 0.25f, Server.game.newNetID(), c);
        Server.game.bullets.add(bullet);
        byte[] bytearray = new byte[25];

        bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
        Bits.putInt(bytearray, 1, bullet.getSpawntick());
        Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
        Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
        Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
        Bits.putFloat(bytearray, 17, bullet.getSpeed());
        Bits.putInt(bytearray, 21, bullet.getNetID());

        for (int i = 0; i < Server.game.clients.size(); i++) {
            Server.serverNetwork.udp.sendPack(bytearray, Server.game.clients.get(i));
        }
    }

    /**
     * Gibt das ServerLevel zurück
     * @return das ServerLevel
     */
    public ServerLevel getLevel() {
        return level;
    }

    /**
     * Gibt den Char am Index x zurück
     */
    public synchronized Char getChar(int x) {
        return chars.get(x);
    }
}
