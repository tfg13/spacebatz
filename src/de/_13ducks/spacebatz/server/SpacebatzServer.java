package de._13ducks.spacebatz.server;

import de._13ducks.spacebatz.server.data.Game;
import de._13ducks.spacebatz.server.gamelogic.MainLoop;
import de._13ducks.spacebatz.server.network.MessageInterpreter;
import de._13ducks.spacebatz.server.network.ServerNetwork;

/**
 * Diese Klasse enthält statische Referenzen auf die wichtigsten Module
 *
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
     * Das Netzwerkmodul des Servers
     */
    public static ServerNetwork serverNetwork = new ServerNetwork();

    /**
     * Einstiegspunkt
     *
     * @param args die Kommandozeilenargumente
     */
    public static void main(String args[]) {
        game = new Game();
        serverNetwork.startServer();

        MainLoop mainLoop = new MainLoop();



        // GO! GO! GO!
        mainLoop.startGameLogic();
    }
}
