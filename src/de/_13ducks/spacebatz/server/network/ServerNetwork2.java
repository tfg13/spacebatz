package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
     * Der Index des Datenpakets, dass der Server als nächstes versendet.
     */
    private int nextOutIndex;

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
			byte[] data = inputPacket.getData();
			byte mode = data[0];
			// NETMODE auswerten:
			switch (mode >>> 6) {
			    case 0:
				// Normales Datenpaket
				Client client = Server.game.clients.get(mode);
				if (client == null) {
				    System.out.println("NET: ignoring packet from unknown client (id: " + mode);
				    continue;
				}
				client.getNetworkConnection().enqueuePacket(new InputPacket(data, client));
				break;
			    case 1:
				// noClient-Modus (sofort verarbeiten)
				byte noClientMode = (byte) (mode & 0x3F);
				switch (noClientMode) {
				    case 0:
					// Connect
					clientRequest(data, inputPacket.getAddress());
					break;
				}
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

    /**
     * Verarbeitet die Anfrage eines Clients, dem Server zu joinen.
     * @param packetData die empfangenen Daten der Anfrage
     * @param origin der Absender der Anfrage
     * @throws IOException falls das Antwort-Senden nicht klappt
     */
    private void clientRequest(byte[] packetData, InetAddress origin) throws IOException {
	//TODO: Check maximum capacity
	// get port
	int port = Bits.getInt(packetData, 1);
	// Craft answer:
	byte[] connectAnswer = new byte[3];
	connectAnswer[0] = (byte) (0x8F | (nextOutIndex >> 8));
	connectAnswer[1] = (byte) (nextOutIndex & 0x000000FF);
	// Vorläufig: ClientID aus altem Netzwerksystem holen:
	//connectAnswer[2] = Server.game.newClientID();
	boolean found = false;
	for (Client client: Server.game.clients.values()) {
	    if (client.getNetworkConnection().getSocket().getInetAddress().equals(origin)) {
		// Gefunden, diese ID nehmen
		connectAnswer[2] = (byte) client.clientID;
		found = true;
		break;
	    }
	}
	if (!found) {
	    System.out.println("ERROR: NET: Cannot find ClientID for request from " + origin + ", connect via old system first!");
	}
	// Senden
	DatagramPacket pack = new DatagramPacket(connectAnswer, connectAnswer.length, origin, port);
	socket.send(pack);
	//TODO: Neuen Client richtig anlegen/einfügen
	System.out.println("INFO: NET: Client " + connectAnswer[2] + " connected, address " + origin + ":" + port);
    }
}
