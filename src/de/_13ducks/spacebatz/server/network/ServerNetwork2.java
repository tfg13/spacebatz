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
import de._13ducks.spacebatz.shared.network.MessageFragmenter;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.shared.network.Utilities;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_INV_ITEM_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_MAP_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_SHOOT;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

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
     * Enthält alle bekannten Netzkommandos, die der Server ausführen kann. Enthält sowohl interne, als auch externe Kommandos.
     */
    static CTSCommand[] cmdMap = new CTSCommand[256];
    /**
     * Liste mit Clients, die verbunden sind darauf warten, von der GameLogic initialisiert zu werden.
     */
    private ConcurrentLinkedQueue<Client> pendingClients = new ConcurrentLinkedQueue<>();

    /**
     * Erstellt ein neues Server-Netzwerksystem
     */
    public ServerNetwork2() {
        cmdMap[0x80] = new CTS_ACK();
        cmdMap[MessageIDs.NET_FRAGMENTED_MESSAGE] = new CTS_FRAGMENTED_MESSAGE();
        cmdMap[MessageIDs.NET_CTS_DEBUG] = new CTS_DEBUG();
        registerCTSCommand(MessageIDs.NET_CTS_MOVE, new CTS_MOVE());
        registerCTSCommand(MessageIDs.NET_CTS_SHOOT, new CTS_SHOOT());
        registerCTSCommand(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_EQUIP, new CTS_EQUIP_ITEM());
        registerCTSCommand(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, new CTS_REQUEST_ITEM_DEQUIP());
        registerCTSCommand(MessageIDs.NET_TCP_CMD_REQUEST_WEAPONSWITCH, new CTS_REQUEST_SWITCH_WEAPON());
        registerCTSCommand(MessageIDs.NET_TCP_CMD_REQUEST_RCON, new CTS_REQUEST_RCON());
        registerCTSCommand(MessageIDs.NET_CTS_USE_ABILITY, new CTS_REQUEST_USE_ABILITY());
        registerCTSCommand(MessageIDs.NET_CTS_MAP_ABILITY, new CTS_REQUEST_MAP_ABILITY());
        registerCTSCommand(MessageIDs.NET_CTS_INVEST_SKILLPOINT, new CTS_INVEST_SKILLPOINT());
        registerCTSCommand(MessageIDs.NET_TCP_CMD_REQUEST_INV_ITEM_MOVE, new CTS_REQUEST_INV_ITEM_MOVE());

        // RCON
        if (Settings.SERVER_ENABLE_RCON) {
            Thread rconThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerSocket rs = new ServerSocket(Settings.SERVER_RCONPORT);

                        while (true) {
                            Socket sock = rs.accept(); // blocks
                            Client client = findClientByAddress(sock.getInetAddress());
                            if (client != null) {
                                Server.debugConsole.addRcon(client, sock.getInputStream(), sock.getOutputStream());
                            } else {
                                System.out.println("WARN: NET: Cannot add RCON, unknown Client with address " + sock.getInetAddress());
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            rconThread.setName("SERVER_RCON_ACC");
            rconThread.setDaemon(true);
            rconThread.start();
        }
    }

    private Client findClientByAddress(InetAddress adr) {
        for (Client c : Server.game.clients.values()) {
            if (c.getNetworkConnection().getInetAddress().equals(adr)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Startet das Netzwerksystem. Spawnt einen neuen Thread
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
                        byte[] data = Utilities.extractData(inputPacket);
                        int mode = data[0] & 0xFF;
                        // NETMODE auswerten:
                        switch (mode >>> 6) {
                            case 0:
                                // Normales Datenpaket
                                Client client = Server.game.clients.get((byte) mode); // Der byte-cast darf auf keinen Fall wegfallen
                                if (client == null) {
                                    System.out.println("NET: ignoring packet from unknown client (id: " + mode);
                                    continue;
                                }
                                CTSPacket cts = new CTSPacket(data, client);
                                cts.preCompute();
                                client.getNetworkConnection().enqueuePacket(cts);
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
                            case 2:
                                // RealTime
                                int rtMode = mode & 0x3F;
                                byte clientID = data[1];
                                Client rtClient = Server.game.clients.get(clientID);
                                if (rtClient == null) {
                                    System.out.println("NET: ignoring RT packet from unknown client (id: " + clientID);
                                    continue;
                                }
                                switch (rtMode) {
                                    case 0:
                                        // Ping-Request. Sofort antworten.
                                        byte[] pongData = new byte[]{(byte) 0x80};
                                        DatagramPacket pongPack = new DatagramPacket(pongData, pongData.length, rtClient.getNetworkConnection().getInetAddress(), rtClient.getNetworkConnection().getPort());
                                        socket.send(pongPack);
                                        break;
                                    case 1:
                                        // Regulärer Client-Disconnect
                                        Server.disconnectClient(rtClient);
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
        // Berechne zu versendende Änderungen:
        Server.sync.tick();

        for (Client c : Server.game.clients.values()) {
            if (c.getNetworkConnection().getPort() != 0) {
                DatagramPacket dPack = c.getNetworkConnection().craftPacket();
                schedulePacket(dPack, c, Bits.getShort(dPack.getData(), 0));
            }
        }

        for (Client c : Server.game.clients.values()) {
            ArrayList<DatagramPacket> sendList = c.getNetworkConnection().getOutBuffer().packetsToSend();
            try {
                for (DatagramPacket packet : sendList) {
                    socket.send(packet);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Registriert einen neuen Befehl beim Netzwerksystem. Zukünfig werden empfangene Kommandos, die die angegebene ID haben von dem gegebenen Kommando bearbeitet. Die gewählte ID muss im erlaubten
     * Bereich für externe Befehle liegen (siehe Netzwerk-Dokumentation)
     *
     * @param cmdID die BefehlsID
     * @param cmd der Befehl selber
     */
    public final void registerCTSCommand(byte cmdID, CTSCommand cmd) {
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
     * Registriert dieses Paket. Das bedeutet, dass der Client dieses Paket erhalten soll. Das Netzwerksystem wird dieses Paket so lange zwischenspeichern und ggf. neu senden, bis der Client den
     * Empfang bestätigt hat.
     *
     * @param dPack Ein Netzwerkpaket, für einen bestimmten Client bestimmt.
     * @param client der Client
     * @param packID die PaketID
     */
    private void schedulePacket(DatagramPacket dPack, Client client, int packID) {
        if (!client.getNetworkConnection().getOutBuffer().registerPacket(dPack, packID)) {
            // Dieser Client ist hoffnungslos
            System.out.println("ERROR: NET: Disconnection client due to outbuffer packet overflow");
            // Client entfernen
            Server.disconnectClient(client);
        }
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
        // get port
        int port = Bits.getInt(packetData, 1);
        // Craft answer:
        byte[] connectAnswer = new byte[8];
        // Paketnummern starten immer bei 0. Das ist möglicherweise nicht perfekt und könnte geändert werden.
        connectAnswer[0] = (byte) 0x40;//connectAnswer[0] = (byte) (0x40 | (nextOutIndex >> 8));
        connectAnswer[1] = (byte) 0;//connectAnswer[1] = (byte) (nextOutIndex & 0x000000FF);

        // neue ClientID vergeben:
        connectAnswer[2] = Server.game.newClientID();

        ServerNetworkConnection clientConnection = new ServerNetworkConnection(origin, port);
        Client newClient = new Client(clientConnection, Server.game.newClientID());
        clientConnection.setClient(newClient);
        Bits.putShort(connectAnswer, 0, (short) clientConnection.nextOutIndex);
        connectAnswer[0] |= 0x40;
        // Aktuellen Tick
        Bits.putInt(connectAnswer, 3, Server.game.getTick());
        connectAnswer[7] = Settings.SERVER_TICKRATE;
        // Senden
        DatagramPacket pack = new DatagramPacket(connectAnswer, connectAnswer.length, origin, port);
        socket.send(pack);
        pendingClients.add(newClient);
        System.out.println("INFO: NET: Client " + connectAnswer[2] + " connected, address " + origin + ":" + port);
    }

    /**
     * Muss synchron von der Mainloop aufgerufen werden, initialisiert die Clients.
     */
    public void initNewClients() {
        Iterator<Client> iter = pendingClients.iterator();
        while (iter.hasNext()) {
            Client c = iter.next();
            Server.game.clientJoined(c);
            iter.remove();
        }
    }

    /**
     * Schiebt einen Befehl in die Warteschlange für ausgehende Befehle. c Befehle automatisch auf. Teilt zu lange
     *
     * @param cmd
     */
    public void queueOutgoingCommand(OutgoingCommand cmd, Client client) {
        if (cmd.data.length >= 128) {
            byte[][] fragments = MessageFragmenter.fragmentMessage((byte) cmd.cmdID, cmd.data);
            for (int i = 0; i < fragments.length; i++) {
                client.getNetworkConnection().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_FRAGMENTED_MESSAGE, fragments[i]));
            }
        } else {
            client.getNetworkConnection().queueOutgoingCommand(cmd);
        }


    }

    CTSCommand getCmdForId(int messageID) {
        return cmdMap[messageID];
    }
}
