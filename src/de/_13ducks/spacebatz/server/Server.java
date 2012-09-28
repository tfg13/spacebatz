/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.FastFindGrid;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.gamelogic.Game;
import de._13ducks.spacebatz.server.gamelogic.MainLoop;
import de._13ducks.spacebatz.server.network.AutoSynchronizer;
import de._13ducks.spacebatz.server.network.ServerNetwork2;

/**
 * Diese Klasse enthält statische Referenzen auf die wichtigsten Module
 *
 * @author michael
 */
public final class Server {

    /**
     * Das laufende Spiel
     */
    public static Game game;
    /**
     * EntityMap, für performante Zugriffe auf Entitys
     */
    public static FastFindGrid entityMap;
    /**
     * Das neue Netzwerkmodul
     */
    public static ServerNetwork2 serverNetwork2 = new ServerNetwork2();
    /**
     * Die automatische Synchronisierung von Entitys und deren Bewegung.
     */
    public static AutoSynchronizer sync = new AutoSynchronizer();
    /**
     * Die Debug-Konsole
     */
    public static DebugConsole debugConsole = new DebugConsole();

    /**
     * Einstiegspunkt
     *
     * @param args die Kommandozeilenargumente
     */
    public static void startServer() {
        game = new Game();
        entityMap = new FastFindGrid(game.getLevel().getSizeX(), game.getLevel().getSizeY());
        serverNetwork2.start();

        MainLoop mainLoop = new MainLoop();

        // GO! GO! GO!
        mainLoop.startGameLogic();
    }

    /**
     * Entfernt einen Client aus dem Spiel. Der Client erhält darüber keinerlei Benachrichtigung.
     *
     * @param client der zu entfernende Client.
     */
    public static void disconnectClient(Client client) {
        Player pl = client.getPlayer();
        game.getEntityManager().removeEntity(pl.netID);
        game.clients.remove(client.clientID);
    }
}
