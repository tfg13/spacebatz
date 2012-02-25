package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.Player;
import de._13ducks.spacebatz.client.graphics.Engine;
import de._13ducks.spacebatz.shared.BulletTypes;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.Level;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Die Empfangskomponente des Netzwerkmoduls
 *
 * @author michael
 */
public class ClientMessageInterpreter {

    /**
     * Puffer für eingehende Tcp-Nachrichten
     */
    private ConcurrentLinkedQueue<ClientTcpMessage> messages;
    /**
     * Dieser Thread empfängt Tcp-Paketee, solange die Engine noch niht geladen ist wenn die Engine gestartet wird übernimmt sie die Tcp-Verarbeitung und dieser Thrad wird deaktiviert
     */
    private Thread initTcpReceiverThread;
    /**
     * gibt an, ob der initTcpReceiverThread noch benötigt wird.
     */
    private boolean initTcpReceiverThreadRun = true;

    /**
     * Initialisiert den Interpreter
     */
    public ClientMessageInterpreter() {
        messages = new ConcurrentLinkedQueue<>();

        // Der Thread der anfangs TcpPackete empfängt:
        initTcpReceiverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (initTcpReceiverThreadRun) {
                    try {

                        for (int i = 0; i < messages.size(); i++) {
                            ClientTcpMessage m = messages.poll();
                            interpretTcpMessage(m.getCmdID(), m.getData());

                            if (!initTcpReceiverThreadRun) {
                                break;
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
        initTcpReceiverThread.setDaemon(true);
        initTcpReceiverThread.start();
    }

    /**
     * Stopft eine neue Nachricht in die Liste, damit der Hauptthread sie später abarbeitet
     *
     * @param m die neue Nachricht
     */
    public void addMessageToQueue(ClientTcpMessage m) {
        messages.add(m);
    }

    /**
     * Wird vom Hauptthread aufgerufen, um alle angesammelten Tcp-Nachrichten zu berechnen
     */
    public void interpretAllTcpMessages() {
        for (int i = 0; i < messages.size(); i++) {
            ClientTcpMessage m = messages.poll();
            interpretTcpMessage(m.getCmdID(), m.getData());
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
                t.setName("CLIENT_ENGINE");
                t.setDaemon(false);
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
            case Settings.NET_TCP_CMD_CHAR_HIT:
                // Char wird von Bullet / angriff getroffen
                int netIDVictim = Bits.getInt(message, 0); // netID von dem, der getroffen wird
                int netIDAttacker = Bits.getInt(message, 4); // netID vom Angreifer / Bullet
                int damage = Bits.getInt(message, 8);

                // Bullet verschwinden lassen
                if (Client.netIDMap.get(netIDAttacker) instanceof Bullet) {
                    Client.netIDMap.remove(netIDAttacker);
                }
                // 
                if (Client.netIDMap.get(netIDVictim) instanceof Player) {
                    Player p = Client.getPlayer();
                    p.setHealthpoints(p.getHealthpoints() - damage);
                    if (p.getHealthpoints() < 0) {
                        // Weil es noch keinen richtigen Respawn gibt, werden die HP hier wieder hochgesetzt
                        p.setHealthpoints(p.getHealthpointsmax());
                    }
                }
                break;
            case Settings.NET_TCP_CMD_TRANSFER_ENEMYTYPES:
                // EnemyTypes empfangen (nur einmal)       
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    EnemyTypes et = (EnemyTypes) is.readObject();
                    Client.enemytypes = et;
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case Settings.NET_TCP_CMD_TRANSFER_BULLETTYPES:
                // BulletTypes empfangen (nur einmal)       
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    BulletTypes bt = (BulletTypes) is.readObject();
                    Client.bullettypes = bt;
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case Settings.NET_TCP_CMD_SPAWN_ITEM:
                // Item wird gedroppt    
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    Item item = (Item) is.readObject();
                    Client.getItemMap().put(item.getNetID(), item);

                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                break;
            case Settings.NET_TCP_CMD_GRAB_ITEM:
                // Item wird aufgesammelt
                int netIDItem2 = Bits.getInt(message, 0); // netID des aufgesammelten Items
                int clientID = Bits.getInt(message, 4); // netID des Spielers, der es aufgesammelt hat
                // Item ins Client-Inventar verschieben, wenn eigene clientID
                if (clientID == Client.getClientID()) {
                    Item item = Client.getItemMap().get(netIDItem2);
                    // Geld oder normales Item?
                    if (item.getStats().itemStats.get("name").equals("Money")) {
                        Client.addMoney(item.getAmount());
                    } else {
                        Client.addToInventory(item);
                    }
                }
                Client.getItemMap().remove(netIDItem2);
                break;
            case Settings.NET_TCP_CMD_TRANSFER_ITEMS:
                // alle aktuell herumliegenden Items an neuen Client geschickt
                try {
                    ObjectInputStream is = new ObjectInputStream(new java.io.ByteArrayInputStream(message));
                    HashMap<Integer, Item> items = (HashMap<Integer, Item>) is.readObject();
                    Client.setItemMap(items);
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case Settings.NET_TCP_CMD_EQUIP_ITEM:
                // Ein Client will ein bestimmtes Item anlegen
                int netIDItem3 = Bits.getInt(message, 0); // netID des  Items
                byte selslot = message[4];
                int clientID3 = Bits.getInt(message, 5); // clientID des Spielers
                if (clientID3 == Client.getClientID()) {
                    Item item = Client.getInventoryItems().get(netIDItem3);
                    Client.getEquippedItems().getEquipslots()[(int) item.getStats().itemStats.get("itemclass")][selslot] = item;
                    for (int i = 0; i < Client.getInventorySlots().length; i++) {
                        if (Client.getInventorySlots()[i] != null && Client.getInventorySlots()[i].equals(item.getInventoryslot())) {
                            Client.getInventorySlots()[i] = null;
                        }
                    }
                    Client.getInventoryItems().values().remove(item);
                }
                break;
            case Settings.NET_TCP_CMD_DEQUIP_ITEM:
                // Ein Client will ein bestimmtes Item ablegen
                int slottype = Bits.getInt(message, 0); // netID des  Items
                byte selslot2 = message[4];
                byte droptoground = message[5];
                int clientID2 = Bits.getInt(message, 6); // clientID des Spielers
                if (clientID2 == Client.getClientID()) {
                    Item item = Client.getEquippedItems().getEquipslots()[slottype][selslot2];
                    Client.getEquippedItems().getEquipslots()[slottype][selslot2] = null;
                    if (droptoground == 0) {
                        Client.addToInventory(item);
                    }
                }
                break;
            case Settings.NET_TCP_CMD_CHANGE_GROUND:
                // Geänderten Boden übernehmen:
                int x = Bits.getInt(message, 0);
                int y = Bits.getInt(message, 4);
                int newGround = Bits.getInt(message, 8);
                Client.currentLevel.getGround()[x][y] = newGround;
                break;
            case Settings.NET_TCP_CMD_SWITCH_WEAPON:
                // Ein Client will andere Waffe auswählen
                int clientid = Bits.getInt(message, 0);
                byte wslot = message[4];
                if (clientid == Client.getClientID()) {
                    Client.getPlayer().setSelectedattack(wslot);
                }
                break;

            default:
                System.out.println("WARNING: Client received unknown TCP-Command " + cmdId);
        }

    }

    /**
     * Interpretiert eine Udp-Nachricht Die Nachricht sollte schnell interpretiert werden
     *
     * @param message die bytes der Nachricht
     */
    public void interpretUdpMessage(byte message[]) {
    }
}
