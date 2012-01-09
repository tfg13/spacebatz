package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.data.Client;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Die Netzwerkkomponente des Servers
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
     * Der Thread der Verbindungen zu Clients aufbaut
     */
    private Thread clientAcceptorThread;

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
        clientAcceptorThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    ServerSocket ss = new ServerSocket(Settings.TCPPORT);

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
        clientAcceptorThread.setName("ClientAcceptorThread");


    }

    /**
     * Stellt eine Verbindung mit jedem anfragenden Client her.
     */
    public void startServer() {
        clientAcceptorThread.start();
    }

    /**
     * Sendet Daten an alle Clients
     * @param message die zu sendenden bytes
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
     * Sendet Daten an einen bestimmten Client
     * @param message die zu sendenden bytes
     * @param client der Empfänger
     */
    public void sendData(byte message[], Client client){
        try {
            client.getNetworkConnection().getSocket().getOutputStream().write(message);
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
