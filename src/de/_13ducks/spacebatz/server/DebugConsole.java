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

import de._13ducks.spacebatz.client.network.NetStats;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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
	// Den normalen Sysout abfangen:
	try {
	    outStream = System.out;
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

			if (loglevel == LOGLEVEL_ALL || (loglevel == LOGLEVEL_WARNING && (warn || err)) || (loglevel == LOGLEVEL_ERROR && err)) {
			    outStream.println(line);
			}
		    }
		}
	    }));
	} catch (Exception ex) {
	}
	debugConsoleThread.start();
	System.out.println("Welcome to ServerDebugConsole!");
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
		switch (words[0]) {
		    case "entitystats":
			outStream.println("Entities in netIDMap: " + Server.game.netIDMap.size());
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
		    case "net_graph":
			if (words.length == 1) {
			    outStream.println("Usage: \"net_graph N\", available modes: Off(0), On(1)");
			} else if (words.length == 2) {
			    int mode = Integer.parseInt(words[1]);
			    if (mode == 0 || mode == 1) {
				NetStats.netGraph = (mode == 1);
			    }
			}
			break;
		    case "list":
			outStream.println("connected clients:");
			for (Client c : Server.game.clients.values()) {
			    outStream.println(c.clientID + ": " + c.getNetworkConnection().getSocket().getInetAddress());
			}
			break;
		    case "resync":
			if (words.length == 2) {
			    int i = Integer.parseInt(words[1]);
			    Server.serverNetwork.udp.resyncClient(Server.game.clients.get(i));
			    outStream.println("Resyncing client " + i);
			} else {
			    outStream.println("Usage: resync CLIENTID (use \"list\")");
			}
			break;
		    case "help":
			outStream.println("Available commands: (Syntax: command arg (optionalarg) - description)");
			outStream.println("entities-at X Y R    - Prints entities within radius R around Point X Y");
			outStream.println("entitystats          - Prints some information about the netIdMap");
			outStream.println("help                 - prints this help");
			outStream.println("list                 - Lists connected clients");
			outStream.println("loglevel (N)         - Prints and allows to set the loglevel");
			outStream.println("net_graph N          - Enables or disables client_netgraphs. (Local only!)");
			outStream.println("resync N             - Resync client with id N");
			outStream.println("su                   - Shut Up! short for \"loglevel 3\"");
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
