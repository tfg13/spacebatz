/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.levelgenerator.LevelGenerator;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.Item;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enthält alle Daten eines Laufenden Spiels
 *
 * @author michael
 */
public class Game {

    /**
     * Liste der verbundenen Clients
     */
    public HashMap<Byte, Client> clients;
    /**
     * Der EntityManager
     */
    private EntityManager entityManager;
    /**
     * HashMap ordnet netID Items zu
     */
    private HashMap<Integer, Item> itemMap;
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
        entityManager = new EntityManager();
        clients = new HashMap<>();
        level = LevelGenerator.generateLevel();
        itemMap = new HashMap<>();
        enemytypes = new EnemyTypes();

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

    /**
     * Wird gerufen, wenn ein neuer Client verbunden wurde
     *
     * @param client der neue Client
     */
    public void clientJoined(Client client) {
        if (client.clientID != -1) {
            Server.msgSender.sendSetClientID(client);
            Server.msgSender.sendLevel(client);
            Server.msgSender.sendAllItems(client, getItemMap());
            Server.msgSender.sendEnemyTypes(client);
            Player player = new Player(level.respawnX, level.respawnY, newNetID(), client);
            Server.msgSender.sendSetPlayer(client, player);
            getEntityManager().netIDMap.put(player.netID, player);
            client.getContext().makeEntityKnown(player.netID);
            // Der Client wird erst in die clientMap eingefügt, wenn das Netzwerksystem von der UDPConnection fertig initialisiert wurde.
            Server.serverNetwork.udp.addClient(client);
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
     * Gibt die bytes der serialisierten Itemliste zurück
     *
     * @return die bytes des serialisierten levels
     */
    public byte[] getSerializedItems() {
        byte[] serializedItems;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os;
        try {
            os = new ObjectOutputStream(bs);
            os.writeObject(getItemMap());
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedItems = bs.toByteArray();
            return serializedItems;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
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
        AIManager.computeMobBehavior(getEntityManager().netIDMap.values());
        // Kollision berechnen:
        CollisionManager.computeCollision();
        // EinheitenPositionen neue berechnen:
        Server.entityMap.calculateEntityPositions();
        // Gegner Spawnen:
        EnemySpawner.tick();
        // Effekte berechnen:
        EffectManager.computeEffects();
    }

    /**
     * Inkrementiert den GameTick
     */
    public void incrementTick() {
        tick++;
    }

    /**
     * Gibt eine neue netID, die noch frei ist, zurück
     *
     * @return eine neue netID
     */
    public final synchronized int newNetID() {
        return nextNetID++;
    }

    public final byte newClientID() {
        Set<Byte> ids = clients.keySet();
        for (byte i = 0; i < Settings.SERVER_MAXPLAYERS; i++) {
            if (!ids.contains(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gibt das ServerLevel zurück
     *
     * @return das ServerLevel
     */
    public ServerLevel getLevel() {
        return level;
    }

    /**
     * @return the itemMap
     */
    public HashMap<Integer, Item> getItemMap() {
        return itemMap;
    }

    /**
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
