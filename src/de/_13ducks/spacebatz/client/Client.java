package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.network.ClientMessageInterpreter;
import de._13ducks.spacebatz.client.network.ClientMessageSender;
import de._13ducks.spacebatz.client.network.ClientNetwork;
import de._13ducks.spacebatz.client.network.ClientTerminal;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.EquippedItems;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.Level;
import java.util.*;

/**
 * Die Hauptklasse des Clients
 * enthält statische Referenzen auf alle Module des Clients (Grafik, Netzwerk, etc).
 *
 * @author michael
 */
public class Client {

    /**
     * Das derzeit laufende Level.
     */
    public static Level currentLevel;
    /**
     * Das Netzwerksystem.
     */
    private static ClientNetwork network;
    /**
     * Der Nachrichteninterpreter.
     */
    private static ClientMessageInterpreter msgInterpreter;
    /**
     * Der Spieler.
     */
    public static Player player;
    /**
     * Aktuell gültiger Gametick.
     * Heißt frozen, weil er während der Grafikberechnung eingefroren wird.
     */
    public static int frozenGametick;
    /**
     * Interner (nur diese Klasse) Gametick, der niemals eingefroren wird.
     */
    private static int gametick;
    /**
     * Die clientID, die uns der Server zugewiesen hat.
     */
    private static byte clientID;
    /**
     * Liste aller Gegnertypen
     */
    public static EnemyTypes enemytypes;
    /**
     * Die Zuordnung von netID zu Char.
     */
    public static HashMap<Integer, Char> netIDMap;
    /**
     * List für alle aktuellen Bullets.
     */
    private static LinkedList<Bullet> bulletList = new LinkedList<>();
    /**
     * Zuordnung von netID zu Item.
     */
    private static HashMap<Integer, Item> itemMap = new HashMap<>();
    /**
     * Items des Inventars des Clients
     */
    private static HashMap<Integer, Item> inventoryItems = new HashMap<>();
    /**
     * belegte Slots des Inventars des Clients
     */
    private static InventorySlot[] inventorySlots = new InventorySlot[Settings.INVENTORY_SIZE];
    /**
     * Hier kommen die Items rein, die gerade angelegt sind
     */
    private static EquippedItems equippedItems;
    /**
     * Wieviel Geld der Spieler gerade besitzt
     */
    private static int money;
    /**
     * Die Logik-Tickrate in ms Abstand zwischen den Ticks.
     */
    public static int tickrate;
    /**
     * Der Timer, der die Ticks hochzählt.
     */
    private static Timer tickTimer;
    /**
     * TCP-Sender zum Server
     */
    private static ClientMessageSender msgSender;
    /**
     * Das Client-Terminal.
     */
    public static ClientTerminal terminal = new ClientTerminal();

    /**
     * Startet den Client und versucht, sich mit der angegebenen IP zu verbinden
     *
     * @param ip die IP, zu der eine Verbindung aufgebaut werden soll
     */
    public static void startClient(String ip) {
	msgSender = new ClientMessageSender();
	msgInterpreter = new ClientMessageInterpreter();
	netIDMap = new HashMap<>();
	network = new ClientNetwork();
	equippedItems = new EquippedItems();
	if (getNetwork().tryConnect(ip)) {
	    // StartRequest per TCP an Server schicken
	    //player = new Player(30, 30);
	} else {
	    System.out.println("ERROR: Can't connect!");
	}
    }

    /**
     * Gibt die Liste mit Bullets zurück
     *
     * @return die Liste der Bullets
     */
    public static LinkedList<Bullet> getBulletList() {
	return bulletList;
    }

    /**
     * Gibt den eigenen Spieler zurück
     *
     * @return der eigene Spieler
     */
    public static Player getPlayer() {
	return player;
    }

    /**
     * Gibt den MessageInterpreter zurück
     *
     * @return der MessageInterpreter
     */
    public static ClientMessageInterpreter getMsgInterpreter() {
	return msgInterpreter;
    }

    /**
     * Gibt die ClientID, die der Server uns zugewiesen hat, zurück
     *
     * @return unsere ClientID
     */
    public static byte getClientID() {
	return clientID;
    }

    /**
     * Setzt die ClientID
     *
     * @param clientID die ClientID, die der Client verwenden soll
     */
    public static void setClientID(byte clientID) {
	Client.clientID = clientID;
    }

    /**
     * Schickt ein vollständiges, gültiges UDP-Paket an den Server.
     *
     * @param packet udp-Paket
     */
    public static void udpOut(byte[] packet) {
	if (packet.length != Settings.NET_UDP_CTS_SIZE || packet[0] != clientID) {
	    throw new IllegalArgumentException("Illegal packet!");
	}
	getNetwork().udpSend(packet);
    }

    /**
     * Leitet UDP-Ticks ans Netzwerksystem weiter.
     */
    public static void udpTick() {
	getNetwork().udpTick();
    }

    /**
     * Gibt das Netzwerkmodul zurück
     *
     * @return das Netzwerkmodul des Clients
     */
    public static ClientNetwork getNetwork() {
	return network;
    }

    public static void updateGametick() {
	frozenGametick = gametick;
    }

    public static void startTickCounting(int serverStartTick) {
	gametick = serverStartTick - (Settings.NET_TICKSYNC_MAXPING / tickrate);
	if (tickTimer == null) {
	    tickTimer = new Timer("Client_tickcounter", true);
	    tickTimer.scheduleAtFixedRate(new TimerTask() {

		@Override
		public void run() {
		    gametick++;
		}
	    }, 0, tickrate);
	}
    }

    public static HashMap<Integer, Item> getItemMap() {
	return itemMap;
    }

    public static void setItemMap(HashMap<Integer, Item> aItemMap) {
	itemMap = aItemMap;
    }

    public static void setInventory(HashMap<Integer, Item> aInventory) {
	inventoryItems = aInventory;
	Iterator<Item> iterator = inventoryItems.values().iterator();
	int i = 0;
	while (iterator.hasNext()) {
	    Item item = iterator.next();
	    inventorySlots[i] = new InventorySlot(item);
	    item.setInventoryslot(inventorySlots[i]);
	    i++;
	}
    }

    /**
     * @return the inventory
     */
    public static HashMap<Integer, Item> getInventoryItems() {
	return inventoryItems;
    }

    /**
     * @return the inventorySlots
     */
    public static InventorySlot[] getInventorySlots() {
	return inventorySlots;
    }

    /**
     * Item in das Spielerinventar aufnehmen
     *
     * @param item Item das geaddet werden soll
     */
    public static void addToInventory(Item item) {
	for (int i = 0; i < inventorySlots.length; i++) {
	    if (inventorySlots[i] == null) {
		inventorySlots[i] = new InventorySlot(item);
		inventoryItems.put(item.getNetID(), item);
		item.setInventoryslot(inventorySlots[i]);
		break;
	    }
	}
    }

    public static void removeFromInventory(int slot) {
	inventoryItems.remove(inventorySlots[slot].getItem().getNetID());
	inventorySlots[slot] = null;
    }

    public static int getMoney() {
	return money;
    }

    public static void addMoney(int amount) {
	money += amount;
    }

    /**
     * @return the equippedSlots
     */
    public static EquippedItems getEquippedItems() {
	return equippedItems;
    }

    /**
     * @param aEquippedSlots the equippedSlots to set
     */
    public static void setEquippedItems(EquippedItems aEquippedItems) {
	equippedItems = aEquippedItems;
    }

    /**
     * @return the msgSender
     */
    public static ClientMessageSender getMsgSender() {
	return msgSender;
    }
}
