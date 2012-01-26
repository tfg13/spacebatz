package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.shared.TcpMessage;
import de._13ducks.spacebatz.EnemyTypes;
import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Die Empfangskomponente des Netzwerkmoduls
 *
 * @author michael
 */
public class MessageInterpreter {

    /**
     * Puffer für eingehende Nachrichten
     */
    private ArrayList<TcpMessage> messages;
    /**
     * Dieser Thread empfängt Tcp-Paketee, solange die Engine noch niht geladen ist
     * wenn die Engine gestartet wird übernimmt sie die Tcp-Verarbeitung und dieser Thrad wird deaktiviert
     */
    private Thread initTcpReceiverThread;
    /**
     * gibt an, ob der initTcpReceiverThread noch benötigt wird.
     */
    private boolean initTcpReceiverThreadRun = true;

    /**
     * Initialisiert den Interpreter
     */
    public MessageInterpreter() {
        messages = new ArrayList<>();

        // Der Thread der anfangs TcpPackete empfängt:
        initTcpReceiverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (initTcpReceiverThreadRun) {
                    try {

                        for (int i = 0; i < getMessages().size(); i++) {
                            interpretTcpMessage(getMessages().get(i).getCmdID(), getMessages().get(i).getData());
                            getMessages().get(i).setComputed();
                            if (!initTcpReceiverThreadRun) {
                                break;
                            }
                        }
                        // Die gelesene Anzahl an Nachrichten löschen.
                        for (int i = 0; i < getMessages().size(); i++) {
                            if (messages.get(i).isComputed()) {
                                messages.remove(i);
                                i--;
                            }

                        }
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });
        initTcpReceiverThread.setName("initialTcpReceiverThread");
        initTcpReceiverThread.start();
    }

    /**
     * Stopft eine neue Nachricht in die Liste, damit der Hauptthread sie später abarbeitet
     * @param m die neue Nachricht
     */
    public void addMessageToQueue(TcpMessage m) {
        getMessages().add(m);
    }

    /**
     * Wird vom Hauptthread aufgerufen, um alle angesammelten Tcp-Nachrichten zu berechnen
     */
    public void interpretAllTcpMessages() {
        for (int i = 0; i < getMessages().size(); i++) {
            interpretTcpMessage(getMessages().get(i).getCmdID(), getMessages().get(i).getData());
        }
        // Die gelesene Anzahl an Nachrichten löschen.
        for (int i = 0; i < getMessages().size(); i++) {
            getMessages().remove(0);
        }
    }

    /**
     * Interpretiert eine TCP-Nachricht
     *
     * @param message die bytes der NAchricht
     */
    public void interpretTcpMessage(byte cmdId, byte message[]) {

        switch (cmdId) {
            case Settings.NET_TCP_CMD_TRANSFER_LEVEL:
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    Level myLevel = (Level) is.readObject();
                    Client.currentLevel = myLevel;
                    System.out.println("Level received and loaded!");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case 21:
                // Player setzen
                Client.player = new Player(Bits.getInt(message, 0));
                Client.netIDMap.put(Client.player.netID, Client.player);
                break;
            case 22:
                // Engine starten:
                Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new Engine().start();
                    }
                });
                t.start();
                Client.startTickCounting(Bits.getInt(message, 0));
                initTcpReceiverThreadRun = false;
                break;
            case 23:
                // ClientID setzen
                Client.setClientID(message[0]);
                break;
            case 27:
                // Tickrate
                int rate = Bits.getInt(message, 0);
                if (rate > 0) {
                    Client.tickrate = rate;
                }
                break;
            case 28:
                // Bullet trifft Char
                int netIDChar = Bits.getInt(message, 0); // netID des getroffenen Chars
                int netIDBullet = Bits.getInt(message, 4); // netID von Bullet
                boolean killed = false; // ob char stirbt
                if (message[8] == 1) {
                    killed = true;
                }

                if (killed) {
                    for (int i = 0; i < Client.getBulletList().size(); i++) {
                        if (Client.getBulletList().get(i).getNetID() == netIDBullet) {
                            Client.getBulletList().remove(i);
                            break;
                        }
                    }
                    Client.netIDMap.remove(netIDChar);
                } else {
                    for (int i = 0; i < Client.getBulletList().size(); i++) {
                        if (Client.getBulletList().get(i).getNetID() == netIDBullet) {
                            Client.getBulletList().remove(i);
                            break;
                        }
                    }
                }
                break;
            case Settings.NET_TCP_CMD_TRANSFER_ENEMYTYPES:
                // EnemyTypes empfangen (nur einmal)       
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    EnemyTypes et = (EnemyTypes) is.readObject();
                    Client.enemytypes = et;
                    System.out.println("Level received and loaded!");
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;

            default:
                System.out.println("WARNING: Client received unknown TCP-Command");
        }

    }

    /**
     * Interpretiert eine Udp-Nachricht Die Nachricht sollte schnell interpretiert werden
     *
     * @param message die bytes der Nachricht
     */
    public void interpretUdpMessage(byte message[]) {
    }

    /**
     * Gibt die NachrihtenListe zurück
     * Synchronisiert, weil Der TcpDAtaReceiverThread und der Hauptthread darauf zugreifen müssen
     * @return die Nachrichetnliste
     */
    public synchronized ArrayList<TcpMessage> getMessages() {
        return messages;
    }
}
