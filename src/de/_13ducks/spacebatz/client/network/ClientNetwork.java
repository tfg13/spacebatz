package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.client.Position;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
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
     * Sendet bytes, longs etc als netzwerkstream
     */
    private ObjectOutputStream sendStream;
    /**
     * Das Client-UDP-Socket
     */
    private DatagramSocket udpSocket;
    /**
     * Die FIFO-Schlange für eingehende Elemente
     */
    private ConcurrentLinkedQueue<DatagramPacket> inputQueue;
    /**
     * Die Adresse des Servers.
     */
    private InetAddress serverAdr;

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
            System.out.println("Connected via " + mySocket.getLocalPort());
            sendStream = new ObjectOutputStream(mySocket.getOutputStream());
            udpSocket = new DatagramSocket(Settings.CLIENT_UDPPORT);
            serverAdr = serverAddress.getAddress();

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
            sendStream.writeByte(cmdId);


            // Packetlänge senden
            sendStream.writeLong(message.length);

            // alle Hunderterblöcke senden:
            byte msg[] = new byte[100];
            for (int b = 0; b < blocks; b++) {
                for (int i = 0; i < 100; i++) {
                    msg[i] = message[b + i];
                }
                sendStream.write(msg);
            }
            // rest senden:
            msg = new byte[rest];
            for (int i = 0; i < rest; i++) {
                msg[i] = message[blocks + i];
            }
            sendStream.write(msg);
            sendStream.flush();
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
                    ObjectInputStream is = new ObjectInputStream(mySocket.getInputStream());

                    while (true) {
                        byte cmdId = is.readByte();
                        long packetSize = is.readLong();
                        byte data[] = new byte[(int) packetSize];
                        is.readFully(data);
                        Client.getMsgInterpreter().interpretTcpMessage(cmdId, data);
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
            case Settings.NET_UDP_CMD_SPAWN_BULLET:
                // Bullet erzeugen
                int spawntick = Bits.getInt(pack, 1);
                float posx = Bits.getFloat(pack, 5);
                float posy = Bits.getFloat(pack, 9);
                float direction = Bits.getFloat(pack, 13);
                float speed = Bits.getFloat(pack, 17);
                int netID = Bits.getInt(pack, 21);
                Bullet bullet = new Bullet(spawntick, new Position(posx, posy), direction, speed, netID);
                Client.getBulletList().add(bullet);
                break;
            default:
                System.out.println("WARNING: UDP with unknown cmd! (was " + cmd + ")");
        }
    }

    /**
     * Sendet ein packet per UDP an den Server
     * @param packet 
     */
    public void udpSend(byte[] packet) {
        try {
            DatagramPacket dp = new DatagramPacket(packet, packet.length, serverAdr, Settings.SERVER_UDPPORT);
            udpSocket.send(dp);
        } catch (IOException ex) {
            System.out.println("ERROR: UDP-Sending failed!");
        }
    }
}
