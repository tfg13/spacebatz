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
     * gibt die Netzwerkverbindung des Clients zurück
     */
    public ServerNetworkConnection getNetworkConnection() {
        return connection;
    }

    void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
