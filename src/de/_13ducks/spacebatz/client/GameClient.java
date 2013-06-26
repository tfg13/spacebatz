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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.data.LogicPlayer;
import de._13ducks.spacebatz.client.logic.ClientQuestManager;
import de._13ducks.spacebatz.client.network.*;
import de._13ducks.spacebatz.client.sound.SilentSoundEngine;
import de._13ducks.spacebatz.client.sound.SoundEngine;
import de._13ducks.spacebatz.client.sound.SoundProvider;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.Level;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Die Hauptklasse des Clients enthält statische Referenzen auf alle Module des Clients (Grafik, Netzwerk, etc).
 *
 * @author michael
 */
public class GameClient {

    /**
     * Das derzeit laufende Level.
     */
    public static Level currentLevel;
    /**
     * Das neue Netzwerksystem.
     */
    private static ClientNetwork2 network2;
    /**
     * Die Grafikengine.
     */
    private static Engine engine;
    /**
     * Der eigene LogicPlayer.
     */
    public static LogicPlayer logicPlayer;
    /**
     * Der eigene Spieler auf der Map.
     */
    public static PlayerCharacter player;
    /**
     * Verwaltet alle Quests.
     */
    public static final ClientQuestManager quests = new ClientQuestManager();
    /**
     * Aktuell gültiger Gametick. Heißt frozen, weil er während der Grafikberechnung eingefroren wird.
     */
    public static int frozenGametick;
    /**
     * Die clientID, die uns der Server zugewiesen hat.
     */
    private static byte clientID;
    /**
     * Liste aller Gegnertypen
     */
    public static EnemyTypes enemytypes;
    /**
     * Die Zuordnung von netID zu Char.
     */
    public static HashMap<Integer, Char> netIDMap;
    /**
     * Die Zuordnung von clientIDs zu logischen Spielern (*nicht* Spielfiguren)
     */
    public static HashMap<Integer, LogicPlayer> players = new HashMap<>();
    /**
     * List für alle aktuellen Bullets.
     */
    private static LinkedList<Bullet> bulletList = new LinkedList<>();
    /**
     * Inventar des Clients
     */
    private static Item[] items = new Item[CompileTimeParameters.INVENTORY_SIZE];
    /**
     * Hier kommen die Items rein, die gerade angelegt sind
     */
    private static EquippedItems equippedItems;
    /**
     * Wieviel Materialien der Spieler gerade besitzt
     */
    private static int materials[] = new int[CompileTimeParameters.NUMBER_OF_MATERIALS];
    /**
     * Das Client-Terminal.
     */
    public static ClientTerminal terminal = new ClientTerminal();
    /**
     * Der Tick, zu dem der Client die Gamelogic berechnet hat.
     */
    private static int logicGameTick;
    /**
     * Audiomodul
     */
    public static SoundProvider soundEngine;

    /**
     * Startet den Client und versucht, sich mit der angegebenen IP zu verbinden Der Client wird dies etwa eine Minute lang versuchen (etwa 60 Verbindungsversuche)
     *
     * @param ip die IP, zu der eine Verbindung aufgebaut werden soll
     */
    public static void startClient(final String ip) throws UnknownHostException {
        network2 = new ClientNetwork2();
        netIDMap = new HashMap<>();
        if (DefaultSettings.CLIENT_SFX_DISABLE_SOUND) {
            soundEngine = new SilentSoundEngine();
        } else {
            soundEngine = new SoundEngine();
        }
        equippedItems = new EquippedItems();

        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Immer weiter versuchen, sich zu connecten
                int triesLeft = 60;
                try {
                    while (!network2.connect(InetAddress.getByName(ip), DefaultSettings.SERVER_UDPPORT2) && triesLeft-- > 0) {
                        System.out.println("Connecting failed. Retrying...");
                    }
                } catch (Exception ex) {
                }

            }
        });
        connectThread.start();
        engine = new Engine();
        engine.start();
    }

    /**
     * Gibt die Liste mit Bullets zurück
     *
     * @return die Liste der Bullets
     */
    public static LinkedList<Bullet> getBulletList() {
        return bulletList;
    }

    /**
     * Gibt die ClientID, die der Server uns zugewiesen hat, zurück
     *
     * @return unsere ClientID
     */
    public static byte getClientID() {
        return clientID;
    }

    /**
     * Setzt die ClientID
     *
     * @param clientID die ClientID, die der Client verwenden soll
     */
    public static void setClientID(byte clientID) {
        GameClient.clientID = clientID;
        logicPlayer = new LogicPlayer(clientID, DefaultSettings.PLAYER_NICKNAME);
        GameClient.players.put((int) clientID, logicPlayer);
    }

    /**
     * Gibt das neue Netzwerkmodul zurück.
     *
     * @return das neue Netzwerkmodul
     */
    public static ClientNetwork2 getNetwork2() {
        return network2;
    }

    public static void updateGametick() {
        frozenGametick = network2.getLogicTick();
    }

    /**
     * Item in das Spielerinventar aufnehmen
     *
     * @param item Item das geaddet werden soll
     */
    public static void addToInventory(Item item) {
        for (int i = 0; i < getItems().length; i++) {
            if (getItems()[i] == null) {
                getItems()[i] = item;
                break;
            }
        }
    }

    public static void removeFromInventory(int slot) {
        getItems()[slot] = null;
    }

    public static int getMaterial(int material) {
        return materials[material];
    }

    public static void setMaterial(int material, int amount) {
        materials[material] = amount;
    }

    /**
     * @return the equippedSlots
     */
    public static EquippedItems getEquippedItems() {
        return equippedItems;
    }

    /**
     * @param aEquippedSlots the equippedSlots to set
     */
    public static void setEquippedItems(EquippedItems aEquippedItems) {
        equippedItems = aEquippedItems;
    }

    /**
     * Liefert die Engine
     *
     * @return the engine
     */
    public static Engine getEngine() {
        return engine;
    }

    /**
     * Setzt die Engine, falls das noch nicht passiert ist.
     *
     * @param aEngine the engine to set
     */
    public static void setEngine(Engine aEngine) {
        if (GameClient.engine != null) {
            throw new IllegalArgumentException("Engine already set!");
        }
        GameClient.engine = aEngine;
    }

    /**
     * @return the items
     */
    public static Item[] getItems() {
        return items;
    }

    /**
     * @param aItems the items to set
     */
    public static void setItems(Item[] aItems) {
        items = aItems;
    }

    /**
     * Berechnet einen Tick für allle Chars.
     */
    static void gameTick() {
        int serverTick = network2.getLogicTick();
        while (logicGameTick <= serverTick) {
            // Input berechnen:
            engine.getGraphics().getInput().asyncInput();
            for (Char c : GameClient.netIDMap.values()) {
                c.tick(logicGameTick);
            }
            logicGameTick++;
        }
    }

    public static void setLogicTick(int serverTick) {
        logicGameTick = serverTick;
    }

    private GameClient() {
    }
}
