package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Senden und verarbeitet UDP-Pakete auf Server-Seite. Lässt sich nicht direkt steuern, versendet automatisch "schnelle" Änderungen bei jedem Tick. Eingehender
 * Input wird gepuffert und beim Tick verarbeitet. Die tick()-Methode muss bei jedem Server-Gametick einmal aufgerufen werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class UDPConnection {

    /**
     * Der UDP-Socket.
     */
    private DatagramSocket socket;
    /**
     * Dieser Thread akzeptiert fortlaufend UDP-Input und stopft ihn in die Queue.
     */
    private Thread inputQueuer;
    /**
     * Queue, die die empfangenen Datenpackete zwischen speichert.
     */
    private ConcurrentLinkedQueue<DatagramPacket> queue;
    /**
     * Hier wird die Zuordnung von ID zu Client in einer schnell abrufbaren Form gespeichert.
     */
    private ConcurrentHashMap<Byte, Client> clientMap;

    public UDPConnection() {
        try {
            queue = new ConcurrentLinkedQueue<>();
            socket = new DatagramSocket(Settings.SERVER_UDPPORT);
            clientMap = new ConcurrentHashMap<>();
            // InputThread starten
            inputQueuer = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        // Immer auf Daten warten
                        while (true) {
                            DatagramPacket p = new DatagramPacket(new byte[Settings.NET_UDP_CTS_SIZE], Settings.NET_UDP_CTS_SIZE);
                            socket.receive(p);
                            queue.add(p);
                        }
                    } catch (IOException ex) {
                    }
                }
            });
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Startet das UDP-Netzwerksystem. Ab jetzt emfängt und sender der Server UDP-Nachrichten.
     */
    public void start() {
        inputQueuer.start();
    }

    /**
     * Verarbeitet den bis zum Zeitpunkt des Aufrufs dieser Methode eingegangenen Input von den Clients. Möglicherweise auch noch Pakete, die während der
     * Verarbeitung der ältesten Pakete reinkommen. Das ist aber nicht garantiert.
     */
    public void receive() {
        computeInput();
    }

    /**
     * Synchronisiert die Clients, indem alle Informationen an sie gesendet werden (UDP), die sie brauchen. Berechnet anhand des Client-Sichtframes, dem
     * MovementSystem und dem ClientContext selbst, was die Clients wissen müssen.
     *
     */
    public void send() {
        sendData();
    }

    /**
     * Fügt einen Client zum (laufenden) Spiel hinzu. Sobald diese Methode zurückkehrt wird der Client vom UDPSystem berücksichtigt, sein Input wird also
     * verarbeitet und er bekommt schnelle Daten geschickt.
     *
     * @param client Das Game-Client-Objekt
     * @param ID die ID des neuen Clients
     */
    public void addClient(Client client, byte ID) {
        clientMap.put(ID, client);
    }

    public void removeClient(byte ID) {
        clientMap.remove(ID);
    }

    /**
     * Verarbeitet gepufferten Input von den Clients. Pakete werden in FIFO-Reihenfolge verarbeitet. Es ist garantiert, dass alle Pakete verarbeitet werden, die
     * bis zum Aufruf dieser Methode angekommen sind. Es ist möglich, aber nicht garantiert, dass Pakete, die während der Verarbeitung dieser Methode noch
     * ankommen, auch noch beachtet werden.
     */
    private void computeInput() {
        Iterator<DatagramPacket> iter = queue.iterator();
        while (iter.hasNext()) {
            DatagramPacket pack = iter.next();
            iter.remove();
            // Verarbeiten:
            computePacket(pack);
        }
    }

    /**
     * Verarbeitet ein einzelnes Datenpaket von einem Client.
     */
    private void computePacket(DatagramPacket packet) {
        // Client raussuchen
        byte[] data = packet.getData();
        Client client = clientMap.get(data[0]);
        if (client != null) {
            // Tick auswerten:
            int tick = Bits.getInt(data, 1);
            // Nur verarbeiten, wenn es neuere Informationen enthält.
            if (tick > client.lastTick) {
                client.lastTick = tick;
                // Input auswerten:
                client.getPlayer().clientMove((data[5] & 0x80) != 0, (data[5] & 0x40) != 0, (data[5] & 0x20) != 0, (data[5] & 0x10) != 0, (data[5] & 0x08) != 0);
            }
        } else {
            System.out.println("INFO: Received data from unknown client. Ignoring. (id was " + data[0] + ")");
        }
    }

    private void sendData() {
        List<Char> chars = Server.game.chars;
        Iterator<Client> iter = clientMap.values().iterator();
        while (iter.hasNext()) {
            Client client = iter.next();
            //TODO: Berechnen, welche chars dieser client wirklich sieht:
            // Für den Anfang einfach mal alle senden:
            int leftToSend = chars.size();
            while (leftToSend > 0) {
                byte[] packet = new byte[32 + (32 * (leftToSend > 15 ? 15 : leftToSend))];
                packChar(packet, chars, (byte) (leftToSend > 15 ? 15 : leftToSend), leftToSend > 15 ? leftToSend - 15 : 0);
                sendPack(packet, client);
                leftToSend -= 15;
            }
        }
    }

    /**
     * Baut und füllt ein byte[]-Packet mit bis zu 15 Chars.
     *
     * @param packet Das byte array, das befüllt wird.
     * @param chars Die Liste mit Chars
     * @param number Die Anzahl von Chars, die eingefüllt werden
     * @param offset Der Index-Offset für die chars-Liste
     */
    private void packChar(byte[] packet, List<Char> chars, byte number, int offset) {
        // Cmd setzen
        packet[0] = Settings.NET_UDP_CMD_NORMAL_CHAR_UPDATE;
        // Tick setzen
        Bits.putInt(packet, 1, Server.game.getTick());
        // Anzahl setzen
        packet[5] = number;
        for (int i = 0; i < number; i++) {
            // NETID
            Bits.putInt(packet, 32 + (i * 32), chars.get(offset + i).netID);
            // X
            Bits.putFloat(packet, 36 + (i * 32), (float) chars.get(offset + i).getX());
            // Y
            Bits.putFloat(packet, 40 + (i * 32), (float) chars.get(offset + i).getY());
            // Rot
            // Rest
        }
    }

    /**
     * Schickt ein Paket an einen Client.
     *
     * @param packet Das Packet
     * @param client Der Client
     */
    public void sendPack(byte[] packet, Client client) {
        DatagramPacket dpack = new DatagramPacket(packet, packet.length, client.getNetworkConnection().getSocket().getInetAddress(), Settings.CLIENT_UDPPORT);
        try {
            socket.send(dpack);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
