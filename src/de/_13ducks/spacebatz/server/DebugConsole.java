package de._13ducks.spacebatz.server;

import de._13ducks.spacebatz.server.data.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Simple Konsole zum Debuggen
 *
 * @author michael
 */
public class DebugConsole {

    private java.io.BufferedReader reader;
    /**
     * Queue, die die Eingaben zwischenspeichert
     */
    private ConcurrentLinkedQueue<String[]> commands;

    /**
     * Konstruktor Erzeugt einen neuen Thread der alle Eingaben zwischenspeichert
     */
    public DebugConsole() {
        reader = new BufferedReader(new java.io.InputStreamReader(System.in));
        commands = new ConcurrentLinkedQueue<>();
        Thread DebugConsoleThread = new Thread(new Runnable() {

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
        DebugConsoleThread.setName("DebugConsoleThread");
        DebugConsoleThread.setDaemon(true);
        DebugConsoleThread.start();
    }

    /**
     * Führt alle eingegebenen Debug-Kommandos aus
     */
    public void executeCommands() {
        while (!commands.isEmpty()) {
            String[] words = commands.poll();
            // Befehle:
            switch (words[0]) {
                case "serverstats":
                    System.out.println("Entities in netIDMap: " + Server.game.netIDMap.size());
                    break;
                case "entities-at":
                    double x = Double.valueOf(words[1]);
                    double y = Double.valueOf(words[2]);
                    LinkedList<Entity> e = Server.entityMap.getEntitiesAroundPoint(x, y);
                    System.out.println("There are " + e.size() + " Entities around point " + x + "/" + y);
                    for (Entity entity : e) {
                        System.out.println("Entity " + entity.netID);
                    }
                    break;
                case "help":
                    System.out.println("Available commands: serverstats, entities-at X Y");
                    break;
                default:
                    System.out.println("Command not recognized.");
                    break;
            }
        }
    }
}
