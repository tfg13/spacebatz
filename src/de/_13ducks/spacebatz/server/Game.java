package de._13ducks.spacebatz.server;

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
     * Konstruktor
     */
    public Game(){
        clients = new ArrayList<Client>();
    }
    
}
