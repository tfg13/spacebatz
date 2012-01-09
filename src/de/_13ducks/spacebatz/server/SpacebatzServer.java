package de._13ducks.spacebatz.server;

/**
 * Diese Klasse enthält statische Referenzen auf die wichtigsten Module
 * @author michael
 */
public final class SpacebatzServer {
    
    /**
     * Globale Referenz auf den MessageInterpreter
     */
    public static MessageInterpreter msgInterpreter = new MessageInterpreter();
    
    /**
     * Das laufende Spiel
     */
    public static Game game;
    
    
    
    /**
     * Einstiegspunkt
     * @param args die Kommandozeilenargumente
     */
    public static void main(String args[]){
        game = new Game();
    }
}
