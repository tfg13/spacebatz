package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Char;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public MainLoop() {
        mainLoopThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < Server.game.chars.size(); i++) {
                        // TODO GAMELOGIC
                        
                        Char c = Server.game.chars.get(i);
                        c.posX += c.dX;
                        c.posY += c.dY;
                        
                        //SpacebatzServer.messageSender.broadcastCharPosition(c);
                        
                    }
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainLoop.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        mainLoopThread.setName("GameLogicThread");
    }

    /**
     * Startet die GameLogic
     */
    public void startGameLogic() {
        mainLoopThread.start();
    }
}
