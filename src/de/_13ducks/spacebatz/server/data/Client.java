package de._13ducks.spacebatz.server.data;

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
     * Auf welchem Servertick die letzen Clientupdates beruhen.
     * Also die größte Ticknummer, die jemals von diesem Client in einem Paket empfangen und verarbeitet wurde.
     */
    public int lastTick;

    /**
     * Konstruktor
     * @param socket der Socket, mit dem der Client verbunden ist 
     */
    public Client(ServerNetworkConnection connection) {
        this.connection = connection;
    }

    /**
     * gibt die Netzwerkverbindung des Clients zurück
     */
    public ServerNetworkConnection getNetworkConnection() {
        return connection;
    }

    /**
     * Gibt den zugehörigen Spieler zurück
     * @return das zugehörige Player Objekt
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Setzt den Hauptcharakter dieses Clients
     * @param player Der neue Player
     */
    void setPlayer(Player player) {
        this.player = player;
    }
}
