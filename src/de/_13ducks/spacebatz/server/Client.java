package de._13ducks.spacebatz.server;

/**
 * Diese Klasse stellt einen Client dar
 * @author michael
 */
public class Client {
    
    /**
     * The NetworkConection of this Client
     */
    private ServerNetworkConnection connection;
    
    /**
     * gibt die Netzwerkverbindung des Clients zurück
     */
    public ServerNetworkConnection getNetworkConnection(){
     return connection;   
        
    }
}
