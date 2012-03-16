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
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Die Netzwerkkomponente des Servers
 *
 * @author michael
 */
public class ServerNetwork {

    /**
     * Liste mit allen verbundenen Clients
     */
    ArrayList<ServerNetworkConnection> connections;
    /**
     * Der Thread der Daten empfängt und an den MessageInterpreter weiterleitet
     */
    private Thread receiveTcpDataThread;
    /**
     * Der Thread der Daten aus dem Tcp-Sendepuffer Sendet
     */
    private Thread sendTcpDataThread;
    /**
     * Der Thread der Verbindungen zu Clients aufbaut
     */
    private Thread clientAcceptorThread;
    /**
     * Das UDP-Netzwerksystem.
     */
    public UDPConnection udp;
    /**
     * Liste mit Clients, die verbunden sind und auf Bearbeitung durch die GameLogic warten
     */
    private ConcurrentLinkedQueue<Client> pendingClients;
    /**
     * Der Puffer für Nachrichten die gesendet werden sollen
     */
    private ConcurrentLinkedQueue<ServerTcpMessage> messageSendBuffer;

    /**
     * Konstruktor initialisiert die Datenempfangs-Threads
     */
    public ServerNetwork() {
        connections = new ArrayList<>();
        udp = new UDPConnection();
        pendingClients = new ConcurrentLinkedQueue<>();
        messageSendBuffer = new ConcurrentLinkedQueue<>();

        // neuer thread, der über TCP daten empfängt:
        receiveTcpDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                receiveTcpData();
            }
        });
        receiveTcpDataThread.setName("DataReceiverThread");
        receiveTcpDataThread.setDaemon(true);
        receiveTcpDataThread.start();

        // neuer thread, der clients akzeptiert:
        clientAcceptorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    ServerSocket ss = new ServerSocket(Settings.SERVER_TCPPORT);

                    while (true) {
                        Socket clientSocket = ss.accept();
                        ServerNetworkConnection client = new ServerNetworkConnection(clientSocket);
                        Client newClient = new Client(client, Server.game.newClientID());
                        client.setClient(newClient);
                        connections.add(client);
                        pendingClients.add(newClient);
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        clientAcceptorThread.setName("ClientAcceptorThread");
        clientAcceptorThread.setDaemon(true);

        sendTcpDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        sendTcpBuffer();
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        sendTcpDataThread.setName("SendTcpDataThread");
        sendTcpDataThread.setDaemon(true);
        sendTcpDataThread.start();


    }

    /**
     * Akzeptiert alle wartenden Clients
     */
    public void acceptPendingClients() {
        for (int i = 0; i < pendingClients.size(); i++) {
            Server.game.clientJoined(pendingClients.poll());
        }
    }

    /**
     * Stellt eine Verbindung mit jedem anfragenden Client her. Forkt sofort.
     */
    public void startServer() {
        clientAcceptorThread.start();
        udp.start();
    }

    /**
     * Sendet Daten an den Server Die Daten werden im Tcp-Puffer gespeichert
     *
     * @param cmdId die commandoid der nachricht
     * @param message der Byte-Array der gesendet werden soll
     */
    public void sendTcpData(byte cmdId, byte message[], Client client) {
        messageSendBuffer.add(new ServerTcpMessage(cmdId, message, client));
    }

    /**
     * Sendet alle Packete im Tcp-Puffer
     */
    private void sendTcpBuffer() {
        try {
            while (!messageSendBuffer.isEmpty()) {
                ServerTcpMessage theMessage = messageSendBuffer.poll();
                byte cmdId = theMessage.getCmdID();
                byte[] message = theMessage.getData();
                Client client = theMessage.getClient();

                int blocks = message.length / 100;
                int rest = message.length % 100;

                // cmdID senden
                client.getNetworkConnection().getSendStream().writeByte(cmdId);
                client.getNetworkConnection().getSendStream().flush();

                // Packetlänge senden
                client.getNetworkConnection().getSendStream().writeLong(message.length);
                client.getNetworkConnection().getSendStream().flush();

                // alle Hunderterblöcke senden:
                byte msg[] = new byte[100];
                for (int b = 0; b < blocks; b++) {
                    for (int i = 0; i < 100; i++) {
                        msg[i] = message[100 * b + i];
                    }
                    client.getNetworkConnection().getSendStream().write(msg);
                    client.getNetworkConnection().getSendStream().flush();
                }

                // rest senden:
                msg = new byte[rest];
                for (int i = 0; i < rest; i++) {
                    msg[i] = message[100 * blocks + i];
                }
                client.getNetworkConnection().getSendStream().write(msg);
                client.getNetworkConnection().getSendStream().flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * liest in einer endlosschleife bytes von allen verbundenen sockets ein
     */
    private void receiveTcpData() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (!connections.isEmpty()) {
                try {
                    for (int c = 0; c < connections.size(); c++) {
                        connections.get(c).receiveData();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
