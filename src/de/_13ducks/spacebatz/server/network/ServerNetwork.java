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
        udp = new UDPConnection();
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
                        Server.game.clientJoined(new Client(client, Server.game.newClientID()));

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
     * Sendet Daten an den Server
     *
     * @param cmdId die commandoid der nachricht
     * @param message der Byte-Array der gesendet werden soll
     */
    public void sendTcpData(byte cmdId, byte message[], Client client) {
        try {
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
                    msg[i] = message[100*b + i];
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
