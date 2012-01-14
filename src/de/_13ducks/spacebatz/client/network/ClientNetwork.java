package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Die Netzwerkkomponente des Clients
 *
 * @author michael
 */
public class ClientNetwork {

    /**
     * Unser Socket
     */
    private Socket mySocket;
    /**
     * Das Client-UDP-Socket
     */
    private DatagramSocket udpSocket;
    /**
     * Die FIFO-Schlange für eingehende Elemente
     */
    private ConcurrentLinkedQueue<DatagramPacket> inputQueue;

    /**
     * Konstruktor
     */
    public ClientNetwork() {
        mySocket = new Socket();
        inputQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * versucht, eine Verbindung zur angegebenen Addresse aufzubauen
     *
     * @return true bei erfolg, false wenn der Verbindungsaufbau scheitert
     */
    public boolean tryConnect(String ipaddress) {
        InetSocketAddress serverAddress = new InetSocketAddress(ipaddress, 10000);
        boolean result = true;
        try {
            mySocket.connect(serverAddress, 10000);
            udpSocket = new DatagramSocket(Settings.CLIENT_UDPPORT);
            receiveData();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * Sendet Daten an den Server
     *
     * @param message der Byte-Array der gessendet werden soll
     */
    public void sendData(byte message[]) {
        try {
            mySocket.getOutputStream().write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Empfängt in einer Endlosschleife Daten
     */
    private void receiveData() {
        Thread receiveDataThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int bytesToRead = 0; // Die bytes die noch gelesen werden müssen
                    byte cmdId = 0;     // Die cmdId
                    byte buffer[] = new byte[0];   // der puffer

                    while (true) {
                        if (bytesToRead == 0) {
                            if (mySocket.getInputStream().available() > 0) {
                                bytesToRead = mySocket.getInputStream().read();
                                buffer = new byte[bytesToRead];
                                cmdId = 0;
                            }
                        } else {
                            if (cmdId == 0) {
                                if (mySocket.getInputStream().available() >= bytesToRead) {
                                    cmdId = (byte) mySocket.getInputStream().read();
                                }
                            } else {
                                if (mySocket.getInputStream().available() >= bytesToRead) {
                                    for (int i = 0; i < bytesToRead; i++) {
                                        buffer[i] = (byte) mySocket.getInputStream().read();
                                    }
                                    Client.getMsgInterpreter().interpretTcpMessage(cmdId, buffer);
                                    bytesToRead = 0;
                                }
                            }

                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        });
        receiveDataThread.setName("ReceiveDataThread");
        receiveDataThread.start();

        Thread udpQueuer = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        DatagramPacket pack = new DatagramPacket(new byte[Settings.NET_UDP_STC_MAX_SIZE], Settings.NET_UDP_STC_MAX_SIZE);
                        udpSocket.receive(pack);
                        inputQueue.add(pack);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        udpQueuer.setName("CLIENT_UDP_QUEUER");
        udpQueuer.setDaemon(true);
        udpQueuer.start();
    }

    /**
     * Muss einmal pro Grafik-Frame aufgerufen werden, verarbeite die bisher angekommenen Pakete.
     */
    public void udpTick() {
        Iterator<DatagramPacket> iter = inputQueue.iterator();
        while (iter.hasNext()) {
            DatagramPacket pack = iter.next();
            iter.remove();
            computePacket(pack);
        }
    }

    /**
     * Verarbeitet ein einzelnes UDP-Paket
     * @param pack ein einzelnes UDP-Paket
     */
    private void computePacket(DatagramPacket pack) {
        byte[] data = pack.getData();
        // Tick prüfen:
        int tick = Bits.getInt(data, 1);
        // Hier ist größer gleich wichtig, der Server kann mehrere schicken!
        if (tick >= Client.gametick) {
            // Paket interessiert uns!
            // Tick hochsetzen
            Client.gametick = tick;
            // Paket nach Typ verarbeiten:
            computeApprovedPacket(data);
        }
    }
    
    /**
     * Verarbeitet verifizierte UDP-Pakete
     * @param pack ein verifiziertes UDP-Paket
     */
    private void computeApprovedPacket(byte[] pack) {
        byte cmd = pack[0];
        switch (cmd) {
            case Settings.NET_UDP_CMD_NORMAL_CHAR_UPDATE:
                // Einheitenkoordinaten updaten.
                // Anzahl enthaltener Einheiten holen:
                byte numberOfCharUpdates = pack[5];
                for (int i = 0; i < numberOfCharUpdates; i++) {
                    int netID = Bits.getInt(pack, 32 + (32 * i));
                    // Diese Einheit bekannt?
                    Char c = Client.netIDMap.get(netID);
                    if (c != null) {
                        // Koordinaten auslesen
                        float newX = Bits.getFloat(pack, 36 + (32 * i));
                        float newY = Bits.getFloat(pack, 40 + (32 * i));
                        // Setzen
                        c.setX(newX);
                        c.setY(newY);
                        //TODO: Andere Daten verwalten, Lags interpolieren.
                    } else {
                        System.out.println("WARNING: CHAR_UPDATE for unknown char (id was " + netID + ")");
                    }
                }
                break;
            default:
                System.out.println("WARNING: UDP with unknown cmd! (was " + cmd + ")");
        }
    }
}
