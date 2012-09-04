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
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Senden und verarbeitet UDP-Pakete auf Server-Seite. Lässt sich nicht direkt steuern, versendet automatisch "schnelle"
 * Änderungen bei jedem Tick. Eingehender Input wird gepuffert und beim Tick verarbeitet. Die tick()-Methode muss bei
 * jedem Server-Gametick einmal aufgerufen werden.
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
    /**
     * Diese Clients müssen (neu) synchronisiert werden.
     */
    private ConcurrentLinkedQueue<Client> syncClients;

    public UDPConnection() {
        try {
            queue = new ConcurrentLinkedQueue<>();
            syncClients = new ConcurrentLinkedQueue<>();
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
                            // Pre-Execute?
                            byte cmd = p.getData()[5];
                            switch (cmd) {
                                case Settings.NET_UDP_CMD_PING:
                                    // Sofort mit PONG antworten:
                                    sendPong(clientMap.get(p.getData()[0]));
                                    break;
                                case Settings.NET_UDP_CMD_TICK_SYNC_PONG:
                                    // Tick-Sync dieses Clients erfolgreich abgeschlossen.
                                    gotSyncAck(p.getData()[0]);
                                    break;
                                default:
                                    // Kein PreExecute, also in die Queue stopfen.
                                    queue.add(p);
                            }
                        }
                    } catch (IOException ex) {
                    }
                }
            });
            inputQueuer.setDaemon(true);
            inputQueuer.setName("UDP_INPUTQUEUER");
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
     * Verarbeitet den bis zum Zeitpunkt des Aufrufs dieser Methode eingegangenen Input von den Clients. Möglicherweise
     * auch noch Pakete, die während der Verarbeitung der ältesten Pakete reinkommen. Das ist aber nicht garantiert.
     */
    public void receive() {
        syncClients();
        computeInput();
    }

    /**
     * Synchronisiert die Clients, indem alle Informationen an sie gesendet werden (UDP), die sie brauchen. Berechnet
     * anhand des Client-Sichtframes, dem MovementSystem und dem ClientContext selbst, was die Clients wissen müssen.
     */
    public void send() {
        sendData();
    }

    /**
     * Fügt einen Client zum (laufenden) Spiel hinzu. Der Gametick dieses Clients wird anschließend synchronisiert.
     * Sobald die synchronisierung abgeschlossen ist, wird der Client beim UDP-Senden berücksichtigt. Erst dann wird er
     * auch in die clientMap des Servers eingetragen.
     *
     * @param client Das Game-Client-Objekt
     */
    public void addClient(Client client) {
        syncClients.add(client);
        //clientMap.put(ID, client);
    }

    public void removeClient(byte ID) {
        clientMap.remove(ID);
    }

    /**
     * (Re-)Synct alle Clients, die in der Sync-Liste stehen. Sendet ein Sync-Paket.
     */
    private void syncClients() {
        for (Client c : syncClients) {
            byte[] b = new byte[9];
            b[0] = Settings.NET_UDP_CMD_TICK_SYNC_PING;
            Bits.putInt(b, 1, Server.game.getTick());
            Bits.putInt(b, 5, Settings.SERVER_TICKRATE);
            sendPack(b, c);
        }
    }

    /**
     * Wir aufgerufen, wenn ein Client-TickSync-ACK ankommt. Setzt den Client auf erfolgreich synchronisiert. Kann
     * gefahrlos mehrfach aufgerufen werden.
     *
     * @param clientID die clientID des clients.
     */
    private void gotSyncAck(byte clientID) {
        Client c = null;
        for (Client cs : syncClients) {
            if (cs.clientID == clientID) {
                c = cs;
                syncClients.remove(cs);
                break;
            }
        }
        if (c != null) {
            // Falls er zum erstem Mal synchronisiert wurde jetzt einfügen:
            if (!clientMap.containsKey(clientID)) {
                clientMap.put(clientID, c);
                Server.game.clients.put(new Byte(clientID), c);
                Server.msgSender.sendStartGame(c);
            }
        }
        System.out.println("client " + clientID + ": (re)sync complete");
    }

    /**
     * Verarbeitet gepufferten Input von den Clients. Pakete werden in FIFO-Reihenfolge verarbeitet. Es ist garantiert,
     * dass alle Pakete verarbeitet werden, die bis zum Aufruf dieser Methode angekommen sind. Es ist möglich, aber
     * nicht garantiert, dass Pakete, die während der Verarbeitung dieser Methode noch ankommen, auch noch beachtet
     * werden.
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
     *
     * @param packet Das zu verarbetende Paket
     */
    private void computePacket(DatagramPacket packet) {
        // Client raussuchen
        byte[] data = packet.getData();
        Client client = clientMap.get(data[0]);
        if (client != null) {
            // Tick auswerten:
            int tick = Bits.getInt(data, 1);
            // Nur verarbeiten, wenn es neuere Informationen enthält.
            if (tick >= client.lastTick) {
                client.lastTick = tick;
                // Input auswerten:
                computeApprovedPacket(data, client);
            } else {
                System.out.println("packetdrop!");
            }
        } else {
            System.out.println("INFO: Received data from unknown client. Ignoring. (id was " + data[0] + ")");
        }
    }

    /**
     * Verarbeitet ein Paket, das als relevant eingestuft wurde.
     *
     * @param data Die Daten des Pakets
     * @param data Der Client, der das Paket geschickt hat.
     */
    private void computeApprovedPacket(byte[] data, Client client) {
        byte cmd = data[5];
        switch (cmd) {
            case Settings.NET_UDP_CMD_INPUT:
                client.getPlayer().clientMove((data[6] & 0x80) != 0, (data[6] & 0x40) != 0, (data[6] & 0x20) != 0, (data[6] & 0x10) != 0);
                break;
            case Settings.NET_UDP_CMD_REQUEST_BULLET:
                client.getPlayer().playerShoot(Bits.getFloat(data, 6));
                break;
            case Settings.NET_UDP_CMD_ACK_MOVE:
                byte ackNumber = data[6];
                for (int i = 0; i < ackNumber; i++) {
                    client.getContext().makeMovementKnown(Bits.getInt(data, 7 + (i * 4)));
                }
                break;
            case Settings.NET_UDP_CMD_ACK_ADD_ENTITY:
                client.getContext().makeEntityKnown(Bits.getInt(data, 6));
                break;
            case Settings.NET_UDP_CMD_ACK_DEL_ENTITY:
                client.getContext().removeEntity(Bits.getInt(data, 6));
                break;
            case Settings.NET_UDP_CMD_PING:
            case Settings.NET_UDP_CMD_TICK_SYNC_PONG:
                // Nichts tun, war schon preexecuted.
                break;
            default:
                System.out.println("WARNING: Received UDP-CTS Packet with unknown cmd-id! (was " + cmd + ")");
        }
    }

    /**
     * Antwortet mit einem Pong.
     *
     * @param client der Ziel-Client
     */
    private void sendPong(Client client) {
        byte[] b = new byte[5];
        b[0] = Settings.NET_UDP_CMD_PONG;
        Bits.putInt(b, 1, Server.game.getTick());
        sendPack(b, client);
    }

    private void sendData() {
        Iterator<Client> iter = clientMap.values().iterator();
        while (iter.hasNext()) {
            Client client = iter.next();
            //TODO: Berechnen, welche entitys dieser client wirklich sieht:
            ArrayList<Entity> update = new ArrayList<>();
            Iterator<Entity> iterE = Server.game.getEntityManager().getEntityIterator();
            while (iterE.hasNext()) {
                Entity e = iterE.next();
                // Kennt der Client diese Einheit?
                if (!client.getContext().knowsEntity(e)) {
                    // Senden
                    sendNewEntity(client, e);
                }
                // Schauen, ob dem Client der Zustand dieser Einheit bekannt ist:
                if (!client.getContext().knowsMovement(e, e.getMovement())) {
                    // Nein, also senden
                    update.add(e);
                    client.getContext().sentMovement(e, e.getMovement());
                }
            }
            // Alle berechneten senden:
            int leftToSend = update.size();
            while (leftToSend > 0) {
                byte[] packet = new byte[32 + (32 * (leftToSend > 15 ? 15 : leftToSend))];
                packEntity(packet, update, (byte) (leftToSend > 15 ? 15 : leftToSend), leftToSend > 15 ? leftToSend - 15 : 0);
                sendPack(packet, client);
                leftToSend -= 15;
            }
            // Dem Client bekannte, aber nichtmehr vorhandene Einheiten löschen
            Iterator<Entity> clientCharIter = client.getContext().knownEntiysIterator();
            while (clientCharIter.hasNext()) {
                Entity e = clientCharIter.next();
                if (!Server.game.getEntityManager().containsEntity(e.netID)) {
                    // Gibts nicht mehr, löschen
                    sendCharEntity(client, e);
                }
            }
        }
    }

    /**
     * Senden dem Client eine Nachricht, die ihn über die Existenz eines neuen Chars informiert.
     *
     * @param client der Client
     * @param e der Char
     */
    private void sendNewEntity(Client client, Entity e) {
        byte[] b = new byte[e.byteArraySize() + 32];
        b[0] = Settings.NET_UDP_CMD_ADD_ENTITY;
        Bits.putInt(b, 1, Server.game.getTick());
        e.netPack(b, 32);
        sendPack(b, client);
    }

    /**
     * Senden dem Client eine Nachricht, die ihn über das Ableben eines Chars informiert.
     *
     * @param client der Client
     * @param e der Char
     */
    private void sendCharEntity(Client client, Entity e) {
        byte[] b = new byte[9];
        b[0] = Settings.NET_UDP_CMD_DEL_ENTITY;
        Bits.putInt(b, 1, Server.game.getTick());
        Bits.putInt(b, 5, e.netID);
        sendPack(b, client);
    }

    /**
     * Baut und füllt ein byte[]-Packet mit bis zu 15 Entitys.
     *
     * @param packet Das byte array, das befüllt wird.
     * @param entitys Die Liste mit Entitys
     * @param number Die Anzahl von Entitys, die eingefüllt werden
     * @param offset Der Index-Offset für die chars-Liste
     */
    private void packEntity(byte[] packet, List<Entity> entitys, byte number, int offset) {
        // Cmd setzen
        packet[0] = Settings.NET_UDP_CMD_NORMAL_ENTITY_UPDATE;
        // Tick setzen
        Bits.putInt(packet, 1, Server.game.getTick());
        // Anzahl setzen
        packet[5] = number;
        for (int i = 0; i < number; i++) {
            Movement m = entitys.get(offset + i).getMovement();
            // NETID
            Bits.putInt(packet, 32 + (i * 32), entitys.get(offset + i).netID);
            // X
            Bits.putFloat(packet, 36 + (i * 32), m.startX);
            // Y
            Bits.putFloat(packet, 40 + (i * 32), m.startY);
            // vecX
            Bits.putFloat(packet, 44 + (i * 32), m.vecX);
            // vecY
            Bits.putFloat(packet, 48 + (i * 32), m.vecY);
            // StartTick
            Bits.putInt(packet, 52 + (i * 32), m.startTick);
            // Speed
            Bits.putFloat(packet, 56 + (i * 32), m.speed);
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

    public void resyncClient(Client sender) {
        if (!syncClients.contains(sender)) {
            syncClients.add(sender);
        }
    }
}
