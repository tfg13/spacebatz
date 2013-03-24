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

import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.network.ServerNetworkConnection;

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
     * Der Nickname des Spielers.
     */
    private String nickName;
    /**
     * Die ID, mit der der Server den Client identifiziert
     */
    public final byte clientID;
    /**
     * Auf welchem Servertick die letzen Clientupdates beruhen. Also die größte Ticknummer, die jemals von diesem Client in einem Paket empfangen und
     * verarbeitet wurde.
     */
    public int lastTick;

    /**
     * Konstruktor
     *
     * @param socket der Socket, mit dem der Client verbunden ist
     */
    public Client(ServerNetworkConnection connection, byte clientID, String nickName) {
        this.connection = connection;
        this.clientID = clientID;
        this.nickName = nickName;
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
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
