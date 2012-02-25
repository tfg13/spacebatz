package de._13ducks.spacebatz.server;

import de._13ducks.spacebatz.server.data.Entity;
import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Eine Konsole zum Debuggen. Wird beim Serverstart normalerweise mitgestartet.
 *
 * @author michael
 */
public class DebugConsole {

    /**
     * Loglevel, printet alles aus.
     */
    private static final int LOGLEVEL_ALL = 0;
    /**
     * Loglevel, printet Warnungen und schlimmer aus.
     */
    private static final int LOGLEVEL_WARNING = 1;
    /**
     * Loglevel, printet Errors und schlimmer aus.
     */
    private static final int LOGLEVEL_ERROR = 2;
    /**
     * Loglevel, printet gar nichts aus.
     */
    private static final int LOGLEVEL_NONE = 3;
    /**
     * Liest von System.in ein.
     */
    private java.io.BufferedReader reader;
    /**
     * Queue, die die Eingaben zwischenspeichert
     */
    private ConcurrentLinkedQueue<String[]> commands;
    /**
     * Das voreingestellte Loglevel
     */
    private int loglevel = LOGLEVEL_ALL;
    /**
     * Der Original-System.out -Stream. Hiermit werden die gefilterten "normalen" Ausgaben dann wirklich ausgegeben.
     */
    private PrintStream outStream;
    /**
     * Ein Reader für System.out. System.out wird bei der Initialisierung im Konstruktor auf diesen Reader umgebogen.
     */
    private BufferedReader outReader;

    /**
     * Konstruktor Erzeugt einen neuen Thread der alle Eingaben zwischenspeichert
     */
    public DebugConsole() {
        reader = new BufferedReader(new java.io.InputStreamReader(System.in));
        commands = new ConcurrentLinkedQueue<>();
        Thread debugConsoleThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        commands.add(reader.readLine().toLowerCase().split("\\s+"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        debugConsoleThread.setName("DebugConsoleThread");
        debugConsoleThread.setDaemon(true);
        Thread outputFilterer = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        String line = outReader.readLine();
                        // Filtern
                        boolean warn = line.toLowerCase().startsWith("warn");
                        boolean err = line.toLowerCase().startsWith("err");

                        if (loglevel == LOGLEVEL_ALL || (loglevel == LOGLEVEL_WARNING && (warn || err)) || (loglevel == LOGLEVEL_ERROR && err)) {
                            outStream.println(line);
                        }
                    }
                } catch (IOException ex) {
                }

            }
        });
        outputFilterer.setName("DebugConsoleThread");
        outputFilterer.setDaemon(true);
        // Den normalen Sysout abfangen:
        try {
            outStream = System.out;
            PipedOutputStream pipeout = new PipedOutputStream();
            PipedInputStream pipein = new PipedInputStream();
            pipeout.connect(pipein);
            //System.setOut(new PrintStream(pipeout));
            outReader = new BufferedReader(new InputStreamReader(pipein));
        } catch (Exception ex) {
        }
        debugConsoleThread.start();
        //outputFilterer.start();
        System.out.println("Welcome to ServerDebugConsole!");
        System.out.println("Please note: Due to bugs output-filtering is disabled.");
    }

    /**
     * Führt alle eingegebenen Debug-Kommandos aus
     */
    public void executeCommands() {
        while (!commands.isEmpty()) {
            String[] words = commands.poll();
            try {
                // Befehle:
                switch (words[0]) {
                    case "entitystats":
                        outStream.println("Entities in netIDMap: " + Server.game.netIDMap.size());
                        break;
                    case "entities-at":
                        double x = Double.valueOf(words[1]);
                        double y = Double.valueOf(words[2]);
                        LinkedList<Entity> e = Server.entityMap.getEntitiesAroundPoint(x, y);
                        outStream.println("There are " + e.size() + " Entities around point " + x + "/" + y);
                        for (Entity entity : e) {
                            outStream.println("Entity " + entity.netID);
                        }
                        break;
                    case "loglevel":
                        if (words.length == 1) {
                            outStream.println("Available loglevels: All(0), Warn+Error only (1), Error only (2), None (3)");
                            outStream.println("Current is " + loglevel);
                        } else if (words.length == 2) {
                            int newlevel = Integer.parseInt(words[1]);
                            if (newlevel >= 0 && newlevel <= 3) {
                                loglevel = newlevel;
                            }
                        }
                        break;
                    case "su":
                        loglevel = LOGLEVEL_NONE;
                        break;
                    case "help":
                        outStream.println("Available commands: (Syntax: command arg (optionalarg) - description)");
                        outStream.println("entitystats      - Prints some information about the entitiymap");
                        outStream.println("entities-at X Y  - Prints entities around Point X Y");
                        outStream.println("loglevel (N)     - Prints and allows to set the loglevel");
                        outStream.println("su               - Shut Up! short for \"loglevel 3\"");
                        outStream.println("help             - prints this help");
                        break;
                    default:
                        outStream.println("Command not recognized. Try help");
                        break;
                }
            } catch (Exception ex) {
                outStream.println("Error computing input. Syntax? (use help)");
            }
        }
    }
}
