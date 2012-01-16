package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
     * Der Status des TCP-Empfangsthreads
     * (cmdId empfangen, PacketLänge empfangen oder DAten empfangen)
     */
    private int tcpReceiverStatus;
    /** cmdId empfangen */
    final static int RECEIVE_CMDID = 0;
    /** Packetgröße empfangen */
    final static int RECEIVE_PACKETSIZE = 1;
    /** PacketDaten empfangen */
    final static int RECEIVE_PACKET = 2;

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

            // KA ob das funktioniert:
            udpSocket = new DatagramSocket();

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
     * @param cmdId die commandoid der nachricht
     * @param message der Byte-Array der gesendet werden soll
     */
    public void sendData(byte cmdId, byte message[]) {
        try {
            int blocks = message.length / 100;
            int rest = message.length % 100;

            // cmdID senden
            mySocket.getOutputStream().write(cmdId);

            // Größe des Packets senden:
            byte sizePacket[] = {(byte) blocks, (byte) rest};
            mySocket.getOutputStream().write(sizePacket);

            // alle Hunderterblöcke senden:
            byte msg[] = new byte[100];
            for (int b = 0; b < blocks; b++) {
                for (int i = 0; i < 100; i++) {
                    msg[i] = message[b + i];
                    mySocket.getOutputStream().write(msg);
                }
            }

            // rest senden:
            msg = new byte[rest];
            for (int i = 0; i < rest; i++) {
                msg[i] = message[blocks + i];
            }
            mySocket.getOutputStream().write(msg);
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
                    int messageSize = 0;            // Die bytes die noch gelesen werden müssen
                    byte cmdId = 0;                 // Die cmdId
                    byte buffer[] = new byte[0];    // der puffer

                    tcpReceiverStatus = RECEIVE_CMDID;

                    while (true) {
                        if (tcpReceiverStatus == RECEIVE_CMDID) {
                            if (mySocket.getInputStream().available() > 0) {
                                cmdId = (byte) mySocket.getInputStream().read();
                                tcpReceiverStatus = RECEIVE_PACKETSIZE;
                            }
                        } else if (tcpReceiverStatus == RECEIVE_PACKETSIZE) {
                            if (mySocket.getInputStream().available() > 1) {
                                int blocks = mySocket.getInputStream().read();
                                int rest = mySocket.getInputStream().read();
                                messageSize = blocks * 100 + rest;
                                tcpReceiverStatus = RECEIVE_PACKET;
                            }
                        } else if (tcpReceiverStatus == RECEIVE_PACKET) {
                            if (mySocket.getInputStream().available() > messageSize) {
                                buffer = new byte[messageSize];
                                for (int i = 0; i < messageSize; i++) {
                                    buffer[i] = (byte) mySocket.getInputStream().read();
                                }
                                Client.getMsgInterpreter().interpretTcpMessage(cmdId, buffer);
                                tcpReceiverStatus = RECEIVE_CMDID;
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
