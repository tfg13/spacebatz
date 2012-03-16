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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.network.ServerNetworkConnection;
import de._13ducks.spacebatz.shared.EquippedItems;

/**
 * Diese Klasse stellt einen Client dar
 *
 * @author michael
 */
public class Client {

    /**
     * The NetworkConection of this Client
     */
    private ServerNetworkConnection connection;
    /**
     * Das zugehörige Player-Objekt
     */
    private Player player;
    /**
     * Die ID, mit der der Server den Client identifiziert
     */
    public final int clientID;
    /**
     * Das Inventar des Clients
     */
    private Inventory inventory;
    /**
     * Hier kommen die Items rein, die gerade angelegt sind
     */
    private EquippedItems equippedItems;
    /**
     * Auf welchem Servertick die letzen Clientupdates beruhen. Also die größte Ticknummer, die jemals von diesem Client in einem Paket empfangen und
     * verarbeitet wurde.
     */
    public int lastTick;
    /**
     * Der Wissensstand des Clients aus Serversicht.
     */
    private ClientContext context;

    /**
     * Konstruktor
     *
     * @param socket der Socket, mit dem der Client verbunden ist
     */
    public Client(ServerNetworkConnection connection, int clientID) {
        this.connection = connection;
        this.clientID = clientID;
        context = new ClientContext();
        inventory = new Inventory();
        equippedItems = new EquippedItems();
    }

    /**
     * gibt die Netzwerkverbindung des Clients zurück
     */
    public ServerNetworkConnection getNetworkConnection() {
        return connection;
    }

    /**
     * Gibt den zugehörigen Spieler zurück
     *
     * @return das zugehörige Player Objekt
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Setzt den Hauptcharakter dieses Clients
     *
     * @param player Der neue Player
     */
    void setPlayer(Player player) {
        this.player = player;
    }

    public ClientContext getContext() {
        return context;
    }

    /**
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @return the equippedItems
     */
    public EquippedItems getEquippedItems() {
        return equippedItems;
    }
}
