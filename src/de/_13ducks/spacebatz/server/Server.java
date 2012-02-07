package de._13ducks.spacebatz.server;

import de._13ducks.spacebatz.server.gamelogic.Game;
import de._13ducks.spacebatz.server.gamelogic.MainLoop;
import de._13ducks.spacebatz.server.network.ServerMessageInterpreter;
import de._13ducks.spacebatz.server.network.ServerMessageSender;
import de._13ducks.spacebatz.server.network.ServerNetwork;

/**
 * Diese Klasse enthält statische Referenzen auf die wichtigsten Module
 *
 * @author michael
 */
public final class Server {

    /**
     * Globale Referenz auf den MessageInterpreter
     */
    public static final ServerMessageInterpreter msgInterpreter = new ServerMessageInterpreter();
    /**
     * Das laufende Spiel
     */
    public static Game game;
    /**
     * Das Netzwerkmodul des Servers
     */
    public static final ServerNetwork serverNetwork = new ServerNetwork();
    
    /**
     * Die Sendekomponente des Netzwerkmoduls
     */
    public static final ServerMessageSender msgSender = new ServerMessageSender();
    
    

    /**
     * Einstiegspunkt
     *
     * @param args die Kommandozeilenargumente
     */
    public static void startServer() {
        game = new Game();
        game.addEnemies();
        game.addPlants();
        serverNetwork.startServer();

        MainLoop mainLoop = new MainLoop();

        // GO! GO! GO!
        mainLoop.startGameLogic();
    }
}
