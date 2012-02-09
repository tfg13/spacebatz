package de._13ducks.spacebatz.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
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
                        commands.add(reader.readLine().split("\\s+"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        DebugConsoleThread.setName("De bugConsoleThread");
        DebugConsoleThread.start();
    }

    /**
     * Führt alle eingegebenen Debug-Kommandos aus
     */
    public void executeCommands() {
        while (!commands.isEmpty()) {
            String[] words = commands.poll();

            // Befehle:           
            if (words[0].equals("help")) {
                System.out.println("There is no help yet.");
            } else {
                System.out.println("Command not recognized.");
            }
        }
    }
}
