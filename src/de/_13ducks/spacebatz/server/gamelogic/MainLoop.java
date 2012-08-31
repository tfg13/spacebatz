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
package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Der MainLoop des Servers
 *
 * @author michael
 */
public class MainLoop {

    /**
     * Der Thread der die Gamelogic ausführt.
     */
    private Timer mainLoopTimer;
    private TimerTask mainLoop;
    /**
     * Ob schonmal Clients connected waren. Wenn ja, und alle Clients gehen offline, dann Server beenden.
     */
    private boolean hadClients = false;

    /**
     * Konstruktor, initialisiert den Thread
     */
    public MainLoop() {
        mainLoopTimer = new Timer("Server_looptimer", false);
        mainLoop = new TimerTask() {

            @Override
            public void run() {
                try {
                    if (checkClientsLeft()) {
			mainLoopTimer.cancel();
                        return;
                    }

                    // Debug-Kommandos ausführen:
                    Server.debugConsole.executeCommands();

                    // Input vom Client holen:
                    Server.serverNetwork.udp.receive();
                    Server.msgInterpreter.interpretAllTcpMessages();
		    Server.serverNetwork2.inTick();
                    // Wartende Clients akzeptieren:
                    Server.serverNetwork.acceptPendingClients();
                    // Gamelogic berechnen:
                    Server.game.gameTick();
                    // Änderungen an die Clients schicken.
                    Server.serverNetwork.udp.send();
		    Server.serverNetwork2.outTick();
                    Server.game.incrementTick();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };


    }

    /**
     * Liefert true, wenn die Mainloop beendet werden soll, weil keine Clients mehr da sind.
     */
    private boolean checkClientsLeft() {
        // Noch Clients da?
        if (hadClients) {
            if (Server.game.clients.isEmpty()) {
                System.out.println("No clients left, shutting server down.");
                return true;
            }
        } else {
            // Jetzt welche da?
            hadClients = !Server.game.clients.isEmpty();
        }
        return false;
    }

    /**
     * Startet die GameLogic
     */
    public void startGameLogic() {
	mainLoopTimer.scheduleAtFixedRate(mainLoop, 0, Settings.SERVER_TICKRATE);
    }
}
