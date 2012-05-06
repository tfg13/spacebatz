package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Das neue, großartige Netzwerksystem.
 * Features:
 * - Alle Übertragungen performant über UDP
 * - Versendet genau 1 Packet pro Tick, verhindert Overhead (dynamische Packetgrößen)
 * - Sorgt automatisch dafür, dass Informationen ankommen.
 * - Komplett verbindungslos, kein langwieriges Aufbauen/Verbinden von Sockets
 * - Automatische Timeout-Erkennung und Behandlung
 * - Automatische Minimierung des Client-Lerps für jeden Client seperat
 * - Automatische Neusynchronisierung bei Verbindungsproblemen
 * - Serverdienste registrierbar, die alle X Ticks Informationen senden
 * - Alle nicht-internen Netzkommandos als Plugins, die auch zur Laufzeit registriert werden können
 * - Automatisches Loadbalancing durch Nachrichten-Prioritätensystem mit Aging
 * - System erkennt Verbindungsprobleme und kann Spiel ggf. zum Speichern auffordern, Clients benachrichtigen etc.
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
     * Die Inputqueue für ankommende Pakete
     */
    private ConcurrentLinkedQueue<DatagramPacket> inputQueue;
    
    /**
     * Erstellt ein neues Server-Netzwerksystem
     */
    public ServerNetwork2() {
	inputQueue = new ConcurrentLinkedQueue<>();
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
			// In die Queue stopfen
			inputQueue.add(inputPacket);
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
	    }
	}, "serv_net");
    }
    
    /**
     * Muss zu Anfang jedes Ticks aufgerufen werden, verarbeitet Input der Clients.
     */
    public void inTick() {
	
    }
    
    /**
     * Muss zum Ende jedes Ticks aufgerufen werden, sendet soebene Berechnete Veränderungen etc an die Clients.
     */
    public void outTick() {
	
    }
}
