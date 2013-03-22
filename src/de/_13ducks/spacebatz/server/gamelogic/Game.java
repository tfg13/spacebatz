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

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.astar.AStarPathfinder;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.ServerLevel;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_LEVEL;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_PLAYER;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_START_ENGINE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_ENEMYTYPES;
import de._13ducks.spacebatz.util.mapgen.MapGen;
import de._13ducks.spacebatz.util.mapgen.MapParameters;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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
     * Liste aller Gegnertypen
     */
    public EnemyTypes enemytypes;
    /**
     * Das Level
     */
    private ServerLevel level;
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
     * Der wegfinder.
     */
    public AStarPathfinder pathfinder;
    /**
     * Der Questmanager.
     */
    public final QuestManager questManager = new QuestManager();
    /**
     * Der Shadowmanager.
     */
    public final ShadowManager shadow = new ShadowManager();

    /**
     * Konstruktor
     */
    public Game() {
        entityManager = new EntityManager();
        clients = new HashMap<>();
        loadOrReloadLevel();
        initMapEntities();
        enemytypes = new EnemyTypes();
        pathfinder = new AStarPathfinder();

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

        // Alle Quests dieses Levels adden
        for (Quest quest : level.quests) {
            questManager.addQuest(quest);
        }
    }

    /**
     * Läd das Level. List die Launchparameter aus, um herauszufinden, ob das
     * gepeicherte, letzte Level geladen werden soll. Wenn ein neues angelegt
     * wird, wird es gespeichert.
     */
    public final void loadOrReloadLevel() {
        MapParameters map = null;
        if ("true".equals(System.getProperty("spacebatz.reloadlevel"))) {
            File lastMap = new File("lastLevel.lvl");
            if (lastMap.canRead()) {
                try {
                    try (BufferedReader reader = new BufferedReader(new FileReader(lastMap))) {
                        StringBuilder mapString = new StringBuilder();
                        String input;
                        while ((input = reader.readLine()) != null) {
                            mapString.append(input);
                        }
                        map = new MapParameters(mapString.toString());
                    }
                } catch (IOException ex) {
                    System.out.println("[ERROR]: Cannot reload level");
                }
            } else {
                System.out.println("[ERROR]: Cannot reload level, file does not exist");
            }
        }
        if (map != null && !map.check()) {
            System.out.println("[ERROR]: Cannot reload level, syntax error! Creating new level...");
            map = null;
        }
        if (map == null) {
            map = new MapParameters();
            // Save map
            try {
                try (FileWriter writer = new FileWriter(new File("lastLevel.lvl"))) {
                    writer.write(map.export());
                    writer.flush();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        level = MapGen.genMap(map);
    }

    /**
     * Kopiert die in der Map vorgegebenen Entities
     * bei Laden in die Spiel-Datenstrukturen, so
     * dass diese sofort nach dem Spielstart vorhanden sind.
     */
    private void initMapEntities() {
        for (Integer i : level.initNetMap.keySet()) {
            entityManager.addEntity(i, level.initNetMap.get(i));
        }
    }

    /**
     * Wird gerufen, wenn ein neuer Client verbunden wurde
     *
     * @param client der neue Client
     */
    public void clientJoined(Client client) {
        if (client.clientID != -1) {
            STC_CHANGE_LEVEL.sendLevel(client);
            STC_TRANSFER_ENEMYTYPES.sendEnemyTypes(client);
            Player player = new Player(level.respawnX, level.respawnY, newNetID(), client);
            STC_SET_PLAYER.sendSetPlayer(client, player);
            getEntityManager().addEntity(player.netID, player);
            // Einfügen und den Client das Spiel starten lassen
            clients.put(new Byte(client.clientID), client);
            STC_START_ENGINE.sendStartGame(client);
            // Dem Client alle aktiven Quests schicken
            Server.game.questManager.newClient(client);
        } else {
            System.out.println("WARNING: Client connected, but Server is full!");
        }
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
        // Quests ticken
        questManager.tick();
        // den tick für alle Entities berechnen:
        Iterator<Entity> iter = getEntityManager().getEntityIterator();
        while (iter.hasNext()) {
            Entity entity = iter.next();
            entity.tick(getTick());
        }
        // EinheitenPositionen neue berechnen:
        Server.entityMap.calculateEntityPositions();
        // Gegner Spawnen:
        EnemySpawner.tick();
        //Tote Entities aufräumen:
        entityManager.removeDisposableEntities();
        // Kollision berechnen: (Muss zuletzt berechnet werden, da sonst Gegner durch Wände laufen können)
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
     *
     * @return eine neue netID
     */
    public final synchronized int newNetID() {
        return nextNetID++;
    }

    public final byte newClientID() {
        Set<Byte> ids = clients.keySet();
        for (byte i = 0; i < DefaultSettings.SERVER_MAXPLAYERS; i++) {
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
     * @return the entityManager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
