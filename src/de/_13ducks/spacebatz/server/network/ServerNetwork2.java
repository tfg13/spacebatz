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
import java.util.ArrayList;
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
    static CTSCommand[] cmdMap = new CTSCommand[256];
    /**
     * Der Index des Datenpakets, dass der Server als nächstes versendet.
     */
    private int nextOutIndex;

    /**
     * Erstellt ein neues Server-Netzwerksystem
     */
    public ServerNetwork2() {
	cmdMap[1] = new CTS_ACK();
    }

    /**
     * Startet das Netzwerksystem.
     * Spawnt einen neuen Thread
     */
    public void start() {
	// Socket eröffnen
	try {
	    socket = new DatagramSocket(Settings.SERVER_UDPPORT2);
	} catch (SocketException ex) {
	    System.out.println("ERROR: NET: Cannot create MulticastSocket, reason:");
	    ex.printStackTrace();
	    return;
	}
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
				client.getNetworkConnection().enqueuePacket(new CTSPacket(data, client));
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
	// DEBUG/TEST
	if (Server.game.getTick() % 100 == 0) {
	    // Manuell ein Paket craften:
	    byte[] manPack = new byte[4];
	    short packId = getAndIncrementNextIndex();
	    Bits.putShort(manPack, 0, packId);
	    // Das Paket hat eine gültige Nummer, keinen MAC und genau ein Kommando, nämlich NOOP.
	    // Senden:
	    for (Client c : Server.game.clients.values()) {
		if (c.getNetworkConnection().getPort() != 0) {
		    DatagramPacket dPack = new DatagramPacket(manPack, manPack.length, c.getNetworkConnection().getSocket().getInetAddress(), c.getNetworkConnection().getPort());
		    schedulePacket(dPack, c, packId);
		}
	    }
	}

	for (Client c : Server.game.clients.values()) {
	    ArrayList<DatagramPacket> sendList = c.getNetworkConnection().getOutBuffer().packetsToSend();
	    for (DatagramPacket packet : sendList) {
		try {
		    socket.send(packet);
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	}
    }

    /**
     * Registriert einen neuen Befehl beim Netzwerksystem.
     * Zukünfig werden empfangene Kommandos, die die angegebene ID haben von dem gegebenen Kommando bearbeitet.
     * Die gewählte ID muss im erlaubten Bereich für externe Befehle liegen (siehe Netzwerk-Dokumentation)
     *
     * @param cmdID die BefehlsID
     * @param cmd der Befehl selber
     */
    public void registerCTSCommand(byte cmdID, CTSCommand cmd) {
	if (cmd == null) {
	    throw new IllegalArgumentException("CTSCommand must not be null!");
	}
	// cmdID: Range prüfen:
	if (cmdID <= 0 || cmdID > 127) {
	    throw new IllegalArgumentException("Illegal cmdID!");
	}
	// Override?
	if (cmdMap[cmdID] != null) {
	    System.out.println("INFO: NET: Overriding cmd " + cmdID);
	}
	cmdMap[cmdID] = cmd;
	System.out.println("INFO: NET: Registered CTS cmd " + cmdID);
    }

    /**
     * Registriert dieses Paket.
     * Das bedeutet, dass der Client dieses Paket erhalten soll.
     * Das Netzwerksystem wird dieses Paket so lange zwischenspeichern und ggf. neu senden, bis der Client den Empfang bestätigt hat.
     *
     * @param dPack Ein Netzwerkpaket, für einen bestimmten Client bestimmt.
     * @param client der Client
     * @param packID die PaketID
     */
    private void schedulePacket(DatagramPacket dPack, Client client, int packID) {
	if (!client.getNetworkConnection().getOutBuffer().registerPacket(dPack, packID)) {
	    // Dieser Client ist hoffnungslos
	    System.out.println("ERROR: NET: Paket output overflow!!!");
	}
    }

    private short getAndIncrementNextIndex() {
	short ret = (short) nextOutIndex++;
	if (nextOutIndex >= Short.MAX_VALUE / 4) {
	    nextOutIndex = 0;
	}
	return ret;
    }

    /**
     * Verarbeitet die Anfrage eines Clients, dem Server zu joinen.
     *
     * @param packetData die empfangenen Daten der Anfrage
     * @param origin der Absender der Anfrage
     * @throws IOException falls das Antwort-Senden nicht klappt
     */
    private void clientRequest(byte[] packetData, InetAddress origin) throws IOException {
	//TODO: Check maximum capacity
	// Erstmal auf das alte Netzwerksystem warten
	try {
	    Thread.sleep(1000);
	} catch (InterruptedException ex) {
	    ex.printStackTrace();
	}
	// get port
	int port = Bits.getInt(packetData, 1);
	// Craft answer:
	byte[] connectAnswer = new byte[3];
	connectAnswer[0] = (byte) (0x40 | (nextOutIndex >> 8));
	connectAnswer[1] = (byte) (nextOutIndex & 0x000000FF);
	// Vorläufig: ClientID aus altem Netzwerksystem holen:
	//connectAnswer[2] = Server.game.newClientID();
	boolean found = false;
	for (Client client : Server.game.clients.values()) {
	    if (client.getNetworkConnection().getSocket().getInetAddress().equals(origin)) {
		// Gefunden, diese ID nehmen
		connectAnswer[2] = client.clientID;
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
	Server.game.clients.get(connectAnswer[2]).getNetworkConnection().setPort(port);
    }
}
