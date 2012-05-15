package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Server-Seite des Netzwerksystems
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ServerNetwork2 {

    /**
     * Das verwendete UDP-Socket.
     */
    private DatagramSocket socket;
    /**
     * Der primäre Thread, der auf UDP-Pakete lauscht.
     */
    private Thread thread;
    /**
     * Enthält alle bekannten Netzkommandos, die der Server ausführen kann.
     * Enthält sowohl interne, als auch externe Kommandos.
     */
    static HashMap<Integer, ServerNetCmd> cmdMap = new HashMap<>();

    /**
     * Erstellt ein neues Server-Netzwerksystem
     */
    public ServerNetwork2() {
    }

    /**
     * Startet das Netzwerksystem.
     * Spawnt einen neuen Thread
     */
    public void start() throws SocketException {
	// Socket eröffnen
	socket = new DatagramSocket(Settings.SERVER_UDPPORT2);
	// Listener-Thread starten
	thread = new Thread(new Runnable() {

	    @Override
	    public void run() {
		try {
		    while (true) {
			DatagramPacket inputPacket = new DatagramPacket(new byte[512], 512);
			// Blocken, bis Paket empfangen
			socket.receive(inputPacket);
			byte mode = inputPacket.getData()[0];
			// NETMODE auswerten:
			switch (mode >>> 6) {
			    case 0:
				// Normales Datenpaket
				Client client = Server.game.clients.get((int) mode);
				if (client == null) {
				    System.out.println("NET: ignoring packet from unknown client (id: " + mode);
				    continue;
				}
				client.getNetworkConnection().enqueuePacket(new InputPacket(inputPacket.getData(), client));
				break;
			}
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	}, "serv_net");
	thread.setDaemon(true);
	thread.start();
    }

    /**
     * Muss zu Anfang jedes Ticks aufgerufen werden, verarbeitet Input der Clients.
     */
    public void inTick() {
	Iterator<Client> iter = Server.game.clients.values().iterator();
	while (iter.hasNext()) {
	    Client client = iter.next();
	    client.getNetworkConnection().computePackets();
	}
    }

    /**
     * Muss zum Ende jedes Ticks aufgerufen werden, sendet soebene Berechnete Veränderungen etc an die Clients.
     */
    public void outTick() {
    }
}
