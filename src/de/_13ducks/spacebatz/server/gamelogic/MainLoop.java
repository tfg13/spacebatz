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
    private Thread mainLoopThread;
    /**
     * Wann der letzte Durchlauf gestartet wurde.
     */
    private long runStart;
    /**
     * Wieviel "Schulden" der Server schon angehäuft hat. Eine Art Tick-Miss-Counter
     */
    private long timeDeficit;
    /**
     * Wieviel die geplante Laufzeit von der richtigen (ohne Runden) abweicht.
     * Hier werden die Rundungsfehler aufsummiert
     */
    private long roundDelta = 0;
    /**
     * Ob schonmal Clients connected waren. Wenn ja, und alle Clients gehen offline, dann Server beenden.
     */
    private boolean hadClients = false;

    /**
     * Konstruktor, initialisiert den Thread
     */
    public MainLoop() {
        mainLoopThread = new Thread(new Runnable() {

            @Override
            public void run() {
                runStart = System.nanoTime();
                while (true) {

                    if (checkClientsLeft()) {
                        break;
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
                    // GameTicks balancieren:
                    balanceTicks();
                    Server.game.incrementTick();
                }
            }
        });
        mainLoopThread.setName("GameLogicThread");
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
     * Versucht, auf die eingestellte Tickrate zu Balancen.
     */
    private void balanceTicks() {
        // Wie lange hat der letzte Durchlauf gedauert?
        long delta = System.nanoTime() - runStart;
        long sleepStart = System.nanoTime();
        // Damit rechnen, dass der nächste Durchlauf auch so lange braucht.
        // Wie lange darf ein Durchlauf maximal gehen:
        long expected = (1000 + roundDelta) / Settings.SERVER_TICKRATE;
        roundDelta = (1000 + roundDelta) % Settings.SERVER_TICKRATE;
        if ((delta / 1000000) < expected) {
            // Es ging kürzer, das ist der (gute!) Normalfall, also ein bisschen Schlafen:
            delta = expected * 1000000 - delta;
            // Defizit eventuell wieder abziehen:
            timeDeficit -= delta;
            if (timeDeficit < 0) {
                timeDeficit = 0;
            }
            try {
                Thread.sleep(delta / 1000000, (int) (delta % 1000000));
            } catch (InterruptedException ex) {
            }
        } else {
            timeDeficit += delta - (expected * 1000000);
            delta = 0;
            // Defizit arg schlimm?
            if (timeDeficit >= Settings.SERVER_TICKMISS_MAX_NANOS) {
                System.out.println("WARNING: Server too slow!");
                timeDeficit = 0;
            }
        }
        // Nächster Run startet zu dem Zeitpunkt, zu dem das Schlafen hätte vorbei sein müssen:
        runStart = sleepStart + delta;
    }

    /**
     * Startet die GameLogic
     */
    public void startGameLogic() {
        mainLoopThread.start();
    }

    /**
     * Berechnet die Spielphysik für einen GameTick
     */
    private void calculateGameTick() {
        Server.game.gameTick();
    }
}
