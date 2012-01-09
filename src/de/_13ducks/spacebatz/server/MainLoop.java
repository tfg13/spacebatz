package de._13ducks.spacebatz.server;

/**
 * Der MainLoop des Servers
 * @author michael
 */
public class MainLoop {
    
    /**
     * Der Thread der die Gamelogic ausführt
     */
    private Thread mainLoopThread;
    
    /**
     * Konstruktor, initialisiert den Thread
     */
    public MainLoop(){
        mainLoopThread = new Thread(new Runnable(){

            @Override
            public void run() {
                while(true){
                    for(int i=0; i<SpacebatzServer.game.chars.size(); i++){
                        
                        // TODO GAMELOGIC
                        
                    }
                }
            }
        });
        mainLoopThread.setName("GameLogicThread"); 
    }
    
    
    /**
     * Startet die GameLogic
     */
    public void startGameLogic(){
        mainLoopThread.start();
    }
    
    
    /**
     * Sendet den Zustand des Spiels an alle Clients
     */
    private void sendGameState(){
        for(int i=0; i<SpacebatzServer.game.clients.size(); i++){
            
        }
    }   
}
