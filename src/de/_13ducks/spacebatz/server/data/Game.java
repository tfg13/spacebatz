package de._13ducks.spacebatz.server.data;

import java.util.ArrayList;

/**
 * Enthält alle Daten eines Laufenden Speils
 * @author michael
 */
public class Game {
    
    /**
     * Liste der verbundenen Clients
     */
    public ArrayList<Client> clients;
    
    /**
     * Liste aller dynamischen Objekte
     * (z.B. Spieler, Mobs, ...)
     */
    public ArrayList<Char> chars;
    
    /**
     * Konstruktor
     */
    public Game(){
        clients = new ArrayList<Client>();
        chars = new ArrayList<>();
    }
    
    /**
     * Wird gerufen, wenn ein neuer Client verbunden wurde
     * @param client der neue Client
     */
    public void clientJoined(Client client){
        clients.add(client);
    }
    
}
