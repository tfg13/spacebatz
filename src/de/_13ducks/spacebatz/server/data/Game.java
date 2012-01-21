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

    public int getTick() {
        return tick;
    }

    /**
     * Berechnet die GameLogic für einen Tick.
     */
    public void gameTick() {

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            float radius = (float) bullet.getSpeed() * (Server.game.getTick() - bullet.getSpawntick());
            float x = (float) bullet.getSpawnposX() + radius * (float) Math.cos(bullet.getDirection());
            float y = (float) bullet.getSpawnposY() + radius * (float) Math.sin(bullet.getDirection());

            for (int j = 0; j < chars.size(); j++) {
                if (Math.abs(x - chars.get(j).posX) < 0.7 && Math.abs(y - chars.get(j).posY) < 0.7) {
                    System.out.println("KOLLISION " + Server.game.getTick());
                }
            }
        }

        // Ab hier: Testcode
        // Bullets spawnen:
        if (tick % 30 == 0) {
            Random random = new Random(System.nanoTime());
            Bullet bullet = new Bullet(tick, 7.0, 7.0, random.nextGaussian() * Math.PI / 16, 0.15f, newNetID());
            bullets.add(bullet);
            byte[] bytearray = new byte[25];

            bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
            Bits.putInt(bytearray, 1, bullet.getSpawntick());
            Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
            Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
            Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
            Bits.putFloat(bytearray, 17, bullet.getSpeed());
            Bits.putInt(bytearray, 21, bullet.getNetID());

            for (int i = 0; i < clients.size(); i++) {
                Server.serverNetwork.udp.sendPack(bytearray, clients.get(i));
            }
        }
        // Enemy verfolgt Spieler:
        for (int i = 0; i < chars.size(); i++) {
            if (chars.get(i) instanceof Enemy) {
                int minplayer = 0;
                double mindist = 12;
                for (int j = 0; j < chars.size(); j++) {
                    if (chars.get(j) instanceof Enemy) {
                        continue;
                    }

                    double px = chars.get(j).posX;
                    double py = chars.get(j).posY;
                    double ex = chars.get(i).posX;
                    double ey = chars.get(i).posY;

                    double thisdist = Math.sqrt((px - ex) * (px - ex) + (py - ey) * (py - ey));
                    if (thisdist < mindist) {
                        minplayer = j;
                        mindist = thisdist;
                    }

                }

                if (chars.get(i).posX < chars.get(minplayer).posX - 1) {
                    chars.get(i).setStillX(chars.get(i).getX() + 0.05f);
                } else if (chars.get(i).posX > chars.get(minplayer).posX + 1) {
                    chars.get(i).setStillX(chars.get(i).getX() - 0.05f);
                }
                if (chars.get(i).posY < chars.get(minplayer).posY - 1) {
                    chars.get(i).setStillY(chars.get(i).getY() + 0.05f);
                } else if (chars.get(i).posY > chars.get(minplayer).posY + 1) {
                    chars.get(i).setStillY(chars.get(i).getY() - 0.05f);
                }

            }
        }
    }

    public void incrementTick() {
        tick++;
    }

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
}
