package de._13ducks.spacebatz.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Die Netzwerkkomponente des Servers
 * @author michael
 */
public class ServerNetwork {

    /**
     * Der Port auf dem der Server auf TCP-Verbindugen wartet
     */
    public static final int TCPPORT = 10000;
    /**
     * Liste mit allen verbundenen Clients
     */
    ArrayList<ServerNetworkConnection> connections;
    /**
     * Der Thread der Daten empfängt und an den MessageInterpreter weiterleitet
     */
    private Thread receiveTcpDataThread;
    /**
     * Der Thread der Verbindungen zu Clients aufbaut
     */
    private Thread ClientAcceptorThread;

    /**
     * Konstruktor
     * initialisiert die Datenempfangs-Threads
     */
    public ServerNetwork() {
        connections = new ArrayList<ServerNetworkConnection>();
        // neuer thread, der daten empfängt:
        receiveTcpDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                receiveTcpData();
            }
        });
        receiveTcpDataThread.setName("DataReceiverThread");

        // neuer thread, der clients akzeptiert:
        ClientAcceptorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    ServerSocket ss = new ServerSocket(TCPPORT);

                    while (true) {
                        Socket clientSocket = ss.accept();
                        ServerNetworkConnection client = new ServerNetworkConnection(clientSocket);
                        connections.add(client);

                        if (!receiveTcpDataThread.isAlive()) {
                            receiveTcpDataThread.start();
                        }

                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        ClientAcceptorThread.setName("ClientAcceptorThread");


    }

    /**
     * Stellt eine Verbindung mit jedem anfragenden Client her.
     */
    public void startServer() {
        ClientAcceptorThread.start();
    }

    /**
     * Sendet Daten an alle Clients
     */
    public void broadcastData(byte message[]) {
        try {
            for (int i = 0; i < connections.size(); i++) {
                connections.get(i).getSocket().getOutputStream().write(message);
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
