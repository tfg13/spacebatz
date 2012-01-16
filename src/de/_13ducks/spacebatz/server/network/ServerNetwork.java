package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
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
     * Das UDP-Netzwerksystem.
     */
    public UDPConnection udp;

    /**
     * Konstruktor
     * initialisiert die Datenempfangs-Threads
     */
    public ServerNetwork() {
        connections = new ArrayList<>();
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
                    ServerSocket ss = new ServerSocket(Settings.SERVER_TCPPORT);

                    while (true) {
                        Socket clientSocket = ss.accept();
                        ServerNetworkConnection client = new ServerNetworkConnection(clientSocket);
                        Server.game.clientJoined(new Client(client));

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
     * Forkt sofort.
     */
    public void startServer() {
        clientAcceptorThread.start();
    }

    /**
     * Sendet Daten via TCP an alle Clients
     * @param message die zu sendenden bytes
     */
    void broadcastData(byte message[]) {
        try {
            for (int i = 0; i < connections.size(); i++) {
                connections.get(i).getSocket().getOutputStream().write(message);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sendet Daten via TCP an einen bestimmten Client
     * @param message die zu sendenden bytes
     * @param client der Empfänger
     * @param cmdId die commandoid der nachricht
     */
    void sendTcpData(byte cmdId, byte message[], Client client) {
        try {

            int blocks = message.length / 100;
            int rest = message.length % 100;

            // cmdID senden
            client.getNetworkConnection().getSocket().getOutputStream().write(cmdId);

            // Größe des Packets senden:
            byte sizePacket[] = {(byte) blocks, (byte) rest};
            client.getNetworkConnection().getSocket().getOutputStream().write(sizePacket);

            // alle hunderter blöcke senden:
            byte msg[] = new byte[100];
            for (int b = 0; b < blocks; b++) {
                for (int i = 0; i < 100; i++) {
                    msg[i] = message[b + i];
                    client.getNetworkConnection().getSocket().getOutputStream().write(msg);
                }
            }

            // rest senden:
            msg = new byte[rest];
            for (int i = 0; i < rest; i++) {
                msg[i] = message[blocks + i];
            }
            client.getNetworkConnection().getSocket().getOutputStream().write(msg);
            
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
