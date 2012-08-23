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
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Die Client-Seite des neuen Netzwerksystems
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientNetwork2 {

    /**
     * Sagt ob wir zu einem Server verbunden sind, und daher Daten senden und empfangen können.
     */
    private boolean connected = false;
    /**
     * Thread, der auf UDP-Pakete lauscht.
     */
    private Thread thread;
    /**
     * Das verwendete Socket.
     */
    private DatagramSocket socket;
    /**
     * Die Nummer des letzten vom Server empfangenen Netzwerkpakets.
     */
    private short lastPkgIndex;
    /**
     * Die Queue der ankommenden Pakete.
     */
    private PriorityBlockingQueue<STCPacket> inputQueue;
    /**
     * Enthält alle bekannten Netzkommandos, die der Server ausführen kann.
     * Enthält sowohl interne, als auch externe Kommandos.
     */
    static STCCommand[] cmdMap = new STCCommand[256];

    /**
     * Erzeugt ein neues Netzwerkmodul.
     */
    public ClientNetwork2() {
    }

    /**
     * Weist das Netzwerksystem an, sich zur Zieladdresse/Port zu verbinden.
     * Liefert true bei erfolgreichem Verbindungsaufbau.
     * Anschließend ist der Setup des Netzwerksystems abgeschlossen und es kann verwendet werden.
     * Wenn false returned wurde, kann diese Methode für weitere Versuche erneut aufgerufen werden.
     * Diese Methode blockt, bis der Server geantwortet hat. (Maximal 5 Sekunden)
     *
     * @param targetAddress die Zieladdresse
     * @param port der Ziel-Port
     * @return true, wenn erfolgreich, sonst false
     */
    public synchronized void connect(final InetAddress targetAddress, final int port) {
	// Vorerst mal einen neuen Thread starten, sollte später nichtmehr möglich sein
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    socket = new DatagramSocket();
		} catch (SocketException ex) {
		    return;// false;
		}
		try {
		    // Initialize-Paket an den Server schicken:
		    byte[] data = new byte[5];
		    Bits.putInt(data, 1, socket.getLocalPort());
		    data[0] = (byte) (1 << 6); // NETMODE auf noClient, connect
		    DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, port);
		    // Antwort-Packet
		    byte[] ansData = new byte[2];
		    DatagramPacket ansPacket = new DatagramPacket(ansData, ansData.length);
		    socket.setSoTimeout(50000);
		    socket.send(packet);
		    try {
			socket.receive(ansPacket);
			socket.setSoTimeout(0);
		    } catch (SocketTimeoutException timeoutEx) {
			// Timeout, ging nicht, Ende.
			socket.close();
			System.out.println("Connecting failed. Request timed out.");
			return;// false;
		    }
		    // Antwort auswerten (erstes Bit):
		    if ((ansData[0] & 0x80) != 0) {
			// Verbindung ok, Parameter auslesen.
			int nextTick = ansData[0] & 0x7F;
			int clientID = ansData[1];
			System.out.println("INFO: NET: Connection established. ClientID " + clientID + ", nextTick " + nextTick);
			initializeReceiver();
			return;// true;
		    } else {
			System.out.println("Connecting failed. Server rejected request. Reason: " + (ansData[0] & 0x7F));
			socket.close();
			return;// false;
		    }
		} catch (IOException ex) {
		    System.out.println("Connecting failed. IOException: " + ex.getLocalizedMessage() + " reason: " + ex.getCause());
		    socket.close();
		    return;// false;
		}
	    }
	});
	t.setName("NETNET_CONNECT_HELP");
	t.setDaemon(true);
	t.start();
    }

    private void initializeReceiver() {
	thread = new Thread(new Runnable() {
	    @Override
	    public void run() {
		try {
		    while (true) {
			DatagramPacket pack = new DatagramPacket(new byte[512], 512);
			// blockt
			socket.receive(pack);
			byte[] data = pack.getData();
			byte mode = data[0];
			// NETMODE auswerten:
			switch (mode >>> 6) {
			    case 0:
				// Normales Datenpaket
				enqueuePacket(new STCPacket(data));
				break;
			    default:
				System.out.println("WARNING: NET: Ignoring packet with unknown netmode (" + (mode >>> 6) + ")");
				break;
			}
		    }
		} catch (IOException ex) {
		    System.out.println("ERROR: NET: Receiving data failed. Details:");
		    ex.printStackTrace();
		}
	    }
	}, "CNET_RECEIVE");
	thread.setDaemon(true);
	thread.start();
    }

    /**
     * Queued ein angekommendes Paket, falls relevant und nicht bereits vorhanden.
     *
     * @param packet das neue Paket.
     */
    private void enqueuePacket(STCPacket packet) {
	// Nicht aufnehmen, wenn zu alt (wrap-around)
	if (Math.abs(packet.getIndex() - lastPkgIndex) > Short.MAX_VALUE / 2 || packet.getIndex() > lastPkgIndex) {
	    if (!inputQueue.contains(packet)) {
		inputQueue.add(packet);
	    }
	}
    }

    /**
     * Muss zu Anfang jedes Ticks aufgerufen werden, verarbeitet Befehle vom Server.
     * Darf erst nach connect aufgerufen werden.
     */
    public void inTick() {
	if (connected) {
	    // Schauen, ob der Index des nächsten Pakets stimmt:
	    while (true) {
		short next = (short) (lastPkgIndex + 1);
		if (next < 0) {
		    next = 0;
		}
		if (inputQueue.peek().getIndex() == next) {
		    STCPacket packet = inputQueue.poll();
		    packet.compute();
		    lastPkgIndex = packet.getIndex();
		} else {
		    break;
		}
	    }
	} else {
	    System.out.println("ERROR: Cannot read inputData, not connected!");
	}
    }

    /**
     * Muss zu Ende jedes Ticks aufgerufen werden, sendet Daten an den Server.
     * Darf erst nach connect aufgerufen werden.
     */
    public void outTick() {
	if (connected) {
	} else {
	    System.out.println("ERROR: Cannot send data, not connected!");
	}
    }
}
