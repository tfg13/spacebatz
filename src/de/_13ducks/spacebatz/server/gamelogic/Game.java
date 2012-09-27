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
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CLIENT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_PLAYER;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_START_ENGINE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_ENEMYTYPES;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_ITEMS;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_LEVEL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPOutputStream;

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

        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            GZIPOutputStream zipOut = new GZIPOutputStream(bs);
            ObjectOutputStream os = new ObjectOutputStream(zipOut);
            os.writeObject(level);
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedLevel = bs.toByteArray();
            System.out.println("Levelsize after GZIP compression: " + serializedLevel.length);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Enemytypes serialisieren, damit es später schnell an Clients gesendet werden kann:
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            GZIPOutputStream zipOut = new GZIPOutputStream(bs);
            ObjectOutputStream os = new ObjectOutputStream(zipOut);
            os.writeObject(enemytypes);
            os.flush();
            bs.flush();
            bs.close();
            os.close();
            serializedEnemyTypes = bs.toByteArray();
            System.out.println("EnemyTypeListsize after GZIP compression: " + serializedEnemyTypes.length);

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
            STC_SET_CLIENT.sendSetClientID(client);
            STC_TRANSFER_LEVEL.sendLevel(client);
            STC_TRANSFER_ITEMS.sendAllItems(client, getItemMap());
            STC_TRANSFER_ENEMYTYPES.sendEnemyTypes(client);
            Player player = new Player(level.respawnX, level.respawnY, newNetID(), client);
            STC_SET_PLAYER.sendSetPlayer(client, player);
            getEntityManager().addEntity(player.netID, player);
            // Einfügen und den Client das Spiel starten lassen
            clients.put(new Byte(client.clientID), client);
            STC_START_ENGINE.sendStartGame(client);
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
        AIManager.computeMobBehavior(getEntityManager().getValues());
        // Kollision berechnen:
        CollisionManager.computeCollision();
        // EinheitenPositionen neue berechnen:
        Server.entityMap.calculateEntityPositions();
        // Gegner Spawnen:
        EnemySpawner.tick();
        // den tick für alle Entities berechnen:
        Iterator<Entity> iter = getEntityManager().getEntityIterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            entity.tick();
        }
        //Tote Entities aufräumen:
        entityManager.removeDisposableEntities();

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
