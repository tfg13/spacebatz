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

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.network.*;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.Level;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Die Hauptklasse des Clients
 * enthält statische Referenzen auf alle Module des Clients (Grafik, Netzwerk, etc).
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
     * Der Nachrichteninterpreter.
     */
    private static InitialMainloop initMainloop;
    /**
     * Die Grafikengine.
     */
    private static Engine engine;
    /**
     * Der Spieler.
     */
    public static PlayerCharacter player;
    /**
     * Aktuell gültiger Gametick.
     * Heißt frozen, weil er während der Grafikberechnung eingefroren wird.
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
     * List für alle aktuellen Bullets.
     */
    private static LinkedList<Bullet> bulletList = new LinkedList<>();
    /**
     * Zuordnung von netID zu Item.
     */
    private static HashMap<Integer, Item> itemMap = new HashMap<>();
    /**
     * Inventar des Clients
     */
    private static Item[] items = new Item[Settings.INVENTORY_SIZE];
    
    /**
     * Hier kommen die Items rein, die gerade angelegt sind
     */
    private static EquippedItems equippedItems;
    /**
     * Wieviel Materialien der Spieler gerade besitzt
     */
    private static int materials[] = new int[Settings.NUMBER_OF_MATERIALS];
    /**
     * Die Logik-Tickrate in ms Abstand zwischen den Ticks.
     */
    public static int tickrate;
    /**
     * Der Timer, der die Ticks hochzählt.
     */
    private static Timer tickTimer;
    /**
     * Das Client-Terminal.
     */
    public static ClientTerminal terminal = new ClientTerminal();

    /**
     * Startet den Client und versucht, sich mit der angegebenen IP zu verbinden
     *
     * @param ip die IP, zu der eine Verbindung aufgebaut werden soll
     */
    public static void startClient(String ip) throws UnknownHostException {
        network2 = new ClientNetwork2();
        initMainloop = new InitialMainloop();
        initMainloop.start();
        netIDMap = new HashMap<>();

        equippedItems = new EquippedItems();
        if (!network2.connect(InetAddress.getByName(ip), Settings.SERVER_UDPPORT2)) {
            throw new RuntimeException("Cannot connect");
        }
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
     * Gibt den eigenen Spieler zurück
     *
     * @return der eigene Spieler
     */
    public static PlayerCharacter getPlayer() {
        return player;
    }

    /**
     * Gibt den MessageInterpreter zurück
     *
     * @return der MessageInterpreter
     */
    public static InitialMainloop getInitialMainloop() {
        return initMainloop;
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

    public static HashMap<Integer, Item> getItemMap() {
        return itemMap;
    }

    public static void setItemMap(HashMap<Integer, Item> aItemMap) {
        itemMap = aItemMap;
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
}
