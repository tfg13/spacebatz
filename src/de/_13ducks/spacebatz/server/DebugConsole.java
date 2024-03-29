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

import de._13ducks.spacebatz.server.ai.astar.PathRequester;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.server.gamelogic.EnemyFactory;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CORRECT_INVENTORY;
import de._13ducks.spacebatz.util.geo.Vector;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
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
     * Ein Stream zum ungefilternen Ausgeben für die interne Nutzung der Debug-Console.
     */
    private PrintStream outStream;
    /**
     * Die Streams zu den rcons.
     */
    private ArrayList<PrintStream> rconOutput;
    /**
     * Die verbundenen rcons.
     */
    private ConcurrentHashMap<Byte, Object[]> rcons;
    /**
     * Sucht nach Fehlern im Sysout und Syserr und gibt den Maphash aus, falls einer auftaucht.
     */
    private boolean errorDetected = false;

    /**
     * Konstruktor Erzeugt einen neuen Thread der alle Eingaben zwischenspeichert
     */
    public DebugConsole() {
        rconOutput = new ArrayList<>();
        rcons = new ConcurrentHashMap<>();
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
        // Den normalen Sysout abfangen:
        try {
            final PrintStream realout = System.out;
            outStream = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    // Eventuell an die rcons verteilen
                    for (PrintStream rcon : rconOutput) {
                        rcon.write(b);
                        rcon.flush();
                    }
                    // Normal ausgeben
                    realout.write(b);
                }
            });
            System.setOut(new PrintStream(new OutputStream() {
                StringBuffer buf = new StringBuffer();

                @Override
                public void write(int b) throws IOException {
                    if (b != '\n' && b != '\r') {
                        buf.append((char) b);
                    } else {
                        String line = buf.toString();
                        buf = new StringBuffer();
                        // Filtern
                        boolean warn = line.toLowerCase().startsWith("warn");
                        boolean err = line.toLowerCase().startsWith("err");
                        if (!errorDetected && (line.toLowerCase().contains("error") || line.toLowerCase().contains("exception"))) {
                            errorDetected = true;
                            outStream.println("DebugConsole: Problem detected. Current Map:");
                            outStream.println(Server.game.getLevel().getHash());
                        }
                        if (loglevel == LOGLEVEL_ALL || (loglevel == LOGLEVEL_WARNING && (warn || err)) || (loglevel == LOGLEVEL_ERROR && err)) {
                            outStream.println(line);
                        }
                    }
                }
            }));
        } catch (Exception ex) {
        }
        // Stderr abfangen:
        try {
            final PrintStream realerr = System.err;
            final PrintStream errStream = new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    // Eventuell an die rcons verteilen
                    for (PrintStream rcon : rconOutput) {
                        rcon.write(b);
                        rcon.flush();
                    }
                    // Normal ausgeben
                    realerr.write(b);
                }
            });
            System.setErr(new PrintStream(new OutputStream() {
                StringBuffer buf = new StringBuffer();

                @Override
                public void write(int b) throws IOException {
                    if (b != '\n' && b != '\r') {
                        buf.append((char) b);
                    } else {
                        String line = buf.toString();
                        buf = new StringBuffer();
                        if (!errorDetected && (line.toLowerCase().contains("error") || line.toLowerCase().contains("exception"))) {
                            errorDetected = true;
                            outStream.println("DebugConsole: Problem detected. Current Map:");
                            outStream.println(Server.game.getLevel().getHash());
                        }
                        errStream.println(line);
                    }
                }
            }));
        } catch (Exception ex) {
        }
        debugConsoleThread.start();
        System.out.println("Welcome to ServerDebugConsole!");
    }

    /**
     * Fügt einen Rcon-Client hinzu.
     *
     * @param c der Client
     * @param i der Stream vom Client
     * @param o der Stream zum Client
     */
    public void addRcon(final Client c, InputStream i, OutputStream o) {
        if (!rcons.containsKey(c.clientID)) {
            Object[] br = new Object[2];
            final BufferedReader input = new BufferedReader(new InputStreamReader(i));
            PrintStream output = new PrintStream(o);
            br[0] = input;
            br[1] = output;
            outStream.println("client " + c.clientID + " connected via rcon");
            rconOutput.add(output);
            rcons.put(c.clientID, br);
            Thread rconReader = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            commands.add(input.readLine().split("\\s+"));

                        }
                    } catch (Exception ex) {
                        rmRcon(c);
                    }
                }
            });
            rconReader.setName("RconReader_client_" + c.clientID);
            rconReader.setDaemon(true);
            rconReader.start();
        }
    }

    /**
     * Entfernt einen Client richtig.
     *
     * @param c der client
     */
    public void rmRcon(Client c) {
        Object[] streams = rcons.get(c.clientID);
        if (streams != null) {
            try {
                ((BufferedReader) streams[0]).close();
            } catch (IOException ex) {
            }
            ((PrintStream) streams[1]).close();
            rconOutput.remove((PrintStream) streams[1]);
            outStream.println("client " + c.clientID + " is no longer connected via rcon");
        }
    }

    /**
     * Führt alle eingegebenen Debug-Kommandos aus
     */
    public void executeCommands() {
        while (!commands.isEmpty()) {
            String[] words = commands.poll();
            if (words.length == 1 && "".equals(words[0])) {
                continue;
            }
            try {
                // Befehle:
                OUTER:
                switch (words[0]) {
                    case "entitystats":
                        outStream.println("Entities in netIDMap: " + Server.game.getEntityManager().getEntityCount());
                        break;
                    case "entities-at":
                        double x = Double.valueOf(words[1]);
                        double y = Double.valueOf(words[2]);
                        double radius = Double.valueOf(words[3]);
                        LinkedList<Entity> e = Server.entityMap.getEntitiesAroundPoint(x, y, radius);
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
                    case "list":
                        outStream.println("connected clients:");
                        for (Client c : Server.game.clients.values()) {
                            outStream.println(c.clientID + ": \"" + c.getNickName() + "\" " + c.getNetworkConnection().getInetAddress());
                        }
                        break;
                    case "spawnitem":
                        int amount = 1;
                        if (words.length >= 2) {
                            amount = Integer.parseInt(words[1]);
                        }
                        for (Client c : Server.game.clients.values()) {
                            Player p = c.getPlayer();
                            for (int i = 0; i < amount; i++) {
                                DropManager.dropItem(p.getX(), p.getY(), 100);
                            }
                        }
                        break;
                    case "spawnenemy":
                        for (Client c : Server.game.clients.values()) {
                            Enemy e1 = EnemyFactory.createEnemy(c.getPlayer().getX(), c.getPlayer().getY(), Server.game.newNetID(), 1);
                            Server.game.getEntityManager().addEntity(e1.netID, e1);
                        }
                        break;
                    case "wall":
                        int targetX = Integer.parseInt(words[1]);
                        int targetY = Integer.parseInt(words[2]);
                        double size = Double.parseDouble(words[3]);
                        Player player = Server.game.clients.values().iterator().next().getPlayer();
                        Server.game.pathfinder.requestPath(new Vector(player.getX(), player.getY()), new Vector(targetX, targetY), new PathRequester() {
                            @Override
                            public void pathComputed(Vector[] path) {
                            }
                        }, size);
                        break;

                    case "oldwall":
                        int targetX1 = Integer.parseInt(words[1]);
                        int targetY1 = Integer.parseInt(words[2]);
                        double size1 = Double.parseDouble(words[3]);
                        Player player1 = Server.game.clients.values().iterator().next().getPlayer();
                        Server.game.pathfinder.requestPath(new Vector(player1.getX(), player1.getY()), new Vector(targetX1, targetY1), new PathRequester() {
                            @Override
                            public void pathComputed(Vector[] path) {
                                for (int i = 0; i < path.length; i++) {
                                    Server.game.getLevel().createDestroyableBlock((int) path[i].x, (int) path[i].y, 10);
                                }
                            }
                        }, size1);
                        break;
                    case "stc_correct_inventory()":
                        for (Client client : Server.game.clients.values()) {
                            STC_CORRECT_INVENTORY.sendCorrectInventory(client.getPlayer().inventory.getInventoryMapping(), client.getPlayer());
                        }


                        break;
                    case "maphash":
                        System.out.println(Server.game.getLevel().getHash());
                        break;
                    case "netstats":
                        // Keine Argumente = einmal printen.
                        for (Client c : Server.game.clients.values()) {
                            c.getNetworkConnection().getStats().printAll();
                        }
                        break;
                    case "kick":
                        if (words.length == 2) {
                            byte id = Byte.parseByte(words[1]);
                            if (!Server.game.clients.containsKey(id)) {
                                outStream.println("Invalid id");
                            } else {
                                Server.serverNetwork2.disconnectClient(Server.game.clients.get(id), (byte) 2);
                                break OUTER;
                            }
                        }
                        outStream.println("Usage: kick id");
                        break;
                    case "help":
                        outStream.println("Available commands: (Syntax: command arg (optionalarg) - description)");
                        outStream.println("entities-at X Y R    - Prints entities within radius R around Point X Y");
                        outStream.println("entitystats          - Prints some information about the netIdMap");
                        outStream.println("help                 - prints this help");
                        outStream.println("kick id              - kick client with given id");
                        outStream.println("list                 - Lists connected clients");
                        outStream.println("loglevel (N)         - Prints and allows to set the loglevel");
                        outStream.println("maphash              - Prints the hash of the current map");
                        outStream.println("netstats (N)         - Prints network statistics");
                        outStream.println("spawnitem            - Spawns an item on every player's position");
                        outStream.println("spawnenemy           - Spawns an enemy on every player's position");
                        outStream.println("su                   - Shut Up! short for \"loglevel 3\"");
                        outStream.println("zone (X Y) (VAR=VAL) - Lists/Sets zone-parameter");
                        break;
                    default:
                        outStream.println("Command not recognized. Try help");
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                outStream.println("Error computing input. Syntax? (use help)");
            }
        }
    }
}
