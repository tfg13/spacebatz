package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Der MainLoop des Servers
 *
 * @author michael
 */
public class MainLoop {

    /**
     * Der Thread der die Gamelogic ausführt.
     */
    private ScheduledThreadPoolExecutor mainLoopTimer;
    private Runnable mainLoop;
    private ScheduledFuture<?> mainLoopTask;
    /**
     * Ob schonmal Clients connected waren. Wenn ja, und alle Clients gehen offline, dann Server beenden.
     */
    private boolean hadClients = false;

    /**
     * Konstruktor, initialisiert den Thread
     */
    public MainLoop() {
        mainLoopTimer = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "Server_Looptimer");
                t.setDaemon(false);
                return t;
            }
        });
        mainLoop = new Runnable() {

            @Override
            public void run() {
                try {
                    if (checkClientsLeft()) {
                        mainLoopTask.cancel(false);
                        mainLoopTimer.shutdown();
                        return;
                    }

                    // Debug-Kommandos ausführen:
                    Server.debugConsole.executeCommands();

                    // Input vom Client holen:
                    Server.serverNetwork.udp.receive();
                    Server.msgInterpreter.interpretAllTcpMessages();
                    // Wartende Clients akzeptieren:
                    Server.serverNetwork.acceptPendingClients();
                    // Gamelogic berechnen:
                    calculateGameTick();
                    // Änderungen an den Server schicken.
                    Server.serverNetwork.udp.send();
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
        mainLoopTask = mainLoopTimer.scheduleAtFixedRate(mainLoop, 0, 1000000000 / Settings.SERVER_TICKRATE, TimeUnit.NANOSECONDS);
    }

    /**
     * Berechnet die Spielphysik für einen GameTick
     */
    private void calculateGameTick() {
        Server.game.gameTick();
    }
}
