package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.network.ServerNetworkConnection;
import java.net.Socket;

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
}
