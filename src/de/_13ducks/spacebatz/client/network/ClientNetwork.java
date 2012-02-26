package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.*;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.LinkedList;
import java.util.ListIterator;

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
     * Eingehende Elemente:
     */
    private LinkedList<DatagramPacket> sortedQueue;
    /**
     * Die Adresse des Servers.
     */
    private InetAddress serverAdr;
    /**
     * Zeitpunkt, zu dem der letzte Ping-Request verschickt wurde.
     */
    private long lastPingTime;

    /**
     * Konstruktor
     */
    public ClientNetwork() {
        mySocket = new Socket();
        sortedQueue = new LinkedList<>();
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
    public void sendTcpData(byte cmdId, byte message[]) {
        try {
            int blocks = message.length / 100;
            int rest = message.length % 100;

            // cmdID senden
            sendStream.writeByte(cmdId);


            // Packetlänge senden
            sendStream.writeShort(message.length);

            // alle Hunderterblöcke senden:
            byte msg[] = new byte[100];
            for (int b = 0; b < blocks; b++) {
                for (int i = 0; i < 100; i++) {
                    msg[i] = message[b * 100 + i];
                }
                sendStream.write(msg);
            }
            // rest senden:
            msg = new byte[rest];
            for (int i = 0; i < rest; i++) {
                msg[i] = message[blocks * 100 + i];
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
                        Client.getMsgInterpreter().addMessageToQueue(new ClientTcpMessage(cmdId, data));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        });
        receiveDataThread.setName("ReceiveDataThread");
        receiveDataThread.setDaemon(true);
        receiveDataThread.start();

        Thread udpQueuer = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        DatagramPacket pack = new DatagramPacket(new byte[Settings.NET_UDP_STC_MAX_SIZE], Settings.NET_UDP_STC_MAX_SIZE);
                        udpSocket.receive(pack);
                        preExecutePacket(pack);
                        insertInQueue(pack);
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
     * Pre-Executed dieses Paket. Manche Pakete müssen schon vor ihrerer eigenen Ausführung etwas tun, z.B. wird für MOVE_UPDATEs sofort ein ACK geschickt
     * Verhindert NICHT die normale, verzögerte Ausführung.
     *
     * @param pack das Datenpaket
     */
    private synchronized void preExecutePacket(DatagramPacket pack) {
        NetStats.pushTickDelay(Bits.getInt(pack.getData(), 1) - Client.frozenGametick);
        byte[] data = pack.getData();
        byte cmd = data[0];
        switch (cmd) {
            case Settings.NET_UDP_CMD_NORMAL_ENTITY_UPDATE:
                // Dem Server sofort bestätigen
                byte numberOfCharUpdates = data[5];
                byte[] ackPack = new byte[4 * numberOfCharUpdates + 6];
                for (int i = 0; i < numberOfCharUpdates; i++) {
                    Bits.putInt(ackPack, 6 + (i * 4), new Movement(Bits.getFloat(data, 36 + (32 * i)), Bits.getFloat(data, 40 + (32 * i)), Bits.getFloat(data, 44 + (32 * i)), Bits.getFloat(data, 48 + (32 * i)), Bits.getInt(data, 52 + (32 * i)), Bits.getFloat(data, 56 + (32 * i))).hashCode());
                }
                ackMove(ackPack);
                break;
            case Settings.NET_UDP_CMD_ADD_ENTITY:
                // Sofort bestätigen
                ackNewChar(Bits.getInt(data, 33));
                break;
            case Settings.NET_UDP_CMD_DEL_ENTITY:
                // Sofort bestätigen
                ackDelChar(Bits.getInt(data, 5));
                break;
            case Settings.NET_UDP_CMD_PONG:
                // Zeitmessung abgeschlossen
                NetStats.ping = (int) (System.currentTimeMillis() - lastPingTime);
                break;
            default:
            // Do nothing, per default werden Pakete nicht preExecuted
        }
    }

    /**
     * Steckt das Paket an die richtige Stelle in die Queue.
     *
     * @param pack
     */
    private synchronized void insertInQueue(DatagramPacket pack) {
        ListIterator<DatagramPacket> iter = sortedQueue.listIterator();
        int tick = Bits.getInt(pack.getData(), 1);
        boolean reachedEnd = true;
        while (iter.hasNext()) {
            if (Bits.getInt(iter.next().getData(), 1) > tick) {
                reachedEnd = false;
                break;
            }
        }
        // Falls wir nicht am Ende waren eines zurück gehen
        if (!reachedEnd) {
            iter.previous();
        }
        iter.add(pack);
    }

    /**
     * Liefert ein neues Paket, das verarbeitet werden kann, falls ein solches existiert.
     *
     * @return ein verarbeitbares Paket, oder null
     */
    private synchronized DatagramPacket getNextComputeable() {
        DatagramPacket p = sortedQueue.peek();
        if (p != null && Bits.getInt(p.getData(), 1) <= Client.frozenGametick) {
            sortedQueue.removeFirst();
            return p;
        }
        return null;
    }

    /**
     * Muss einmal pro Grafik-Frame aufgerufen werden, verarbeitet alle für den aktuellen Gametick relevanten Pakete.
     */
    public void udpTick() {
        DatagramPacket p;
        while ((p = getNextComputeable()) != null) {
            computePacket(p);
        }
        // Einmal pro Sekunde:
        if (Client.frozenGametick % Client.tickrate == 0) {
            lastPingTime = System.currentTimeMillis();
            // UDP-Pingpaket schicken
            byte[] udp = new byte[6];
            udp[0] = Client.getClientID();
            Bits.putInt(udp, 1, Client.frozenGametick);
            udp[5] = Settings.NET_UDP_CMD_PING;
            udpSend(udp);
        }
    }

    /**
     * Verarbeitet ein einzelnes UDP-Paket
     *
     * @param pack ein einzelnes UDP-Paket
     */
    private void computePacket(DatagramPacket pack) {
        byte[] data = pack.getData();
        //TODO: Client-Tickrate synchronisieren, viel zu alte Pakete löschen.
        // Paket nach Typ verarbeiten:
        computeApprovedPacket(data);
    }

    /**
     * Verarbeitet verifizierte UDP-Pakete
     *
     * @param pack ein verifiziertes UDP-Paket
     */
    private void computeApprovedPacket(byte[] pack) {
        NetStats.inPack(pack.length);
        byte cmd = pack[0];
        switch (cmd) {
            case Settings.NET_UDP_CMD_NORMAL_ENTITY_UPDATE:
                // Einheitenbewegungen updaten.
                // Anzahl enthaltener Einheiten holen:
                byte numberOfCharUpdates = pack[5];
                for (int i = 0; i < numberOfCharUpdates; i++) {
                    int netID = Bits.getInt(pack, 32 + (32 * i));
                    // Diese Einheit bekannt?
                    Char c = Client.netIDMap.get(netID);
                    if (c != null) {
                        // Bewegung setzen:
                        Movement m = new Movement(Bits.getFloat(pack, 36 + (32 * i)), Bits.getFloat(pack, 40 + (32 * i)), Bits.getFloat(pack, 44 + (32 * i)), Bits.getFloat(pack, 48 + (32 * i)), Bits.getInt(pack, 52 + (32 * i)), Bits.getFloat(pack, 56 + (32 * i)));
                        c.applyMove(m);
                    } else {
                        System.out.println(System.nanoTime() + "WARNING: Ignoring propably ACK'ed moveupdate! (Char was: " + netID + ")");
                    }
                }
                break;
            case Settings.NET_UDP_CMD_ADD_ENTITY:
                if (!Client.netIDMap.containsKey(Bits.getInt(pack, 33))) {
                    byte type = pack[32];
                    switch (type) {
                        case 2:
                            Player pl = new Player(Bits.getInt(pack, 33));
                            Client.netIDMap.put(pl.netID, pl);
                            break;
                        case 3:
                            Enemy en = new Enemy(Bits.getInt(pack, 33), Bits.getInt(pack, 37));
                            Client.netIDMap.put(en.netID, en);
                            break;
                        case 4:
                            Bullet bu = new Bullet(Bits.getInt(pack, 33), Bits.getInt(pack, 37));
                            Client.netIDMap.put(bu.netID, bu);
                            break;
                        default:
                            System.out.println("WARN: Unknown charTypeID (was " + type + ")");
                    }
                }
                break;
            case Settings.NET_UDP_CMD_DEL_ENTITY:
                if (Client.netIDMap.containsKey(Bits.getInt(pack, 5))) {
                    // Löschen
                    Client.netIDMap.remove(Bits.getInt(pack, 5));
                }
                break;
            case Settings.NET_UDP_CMD_PONG:
                // Nichts tun.
                break;
            default:
                System.out.println("WARNING: UDP with unknown cmd! (was " + cmd + ")");
        }
    }

    /**
     * Bestätigt dem Server den erhalt dieses Movements
     *
     * @param m das erhaltene Movement
     */
    private void ackMove(byte[] b) {
        b[0] = Client.getClientID();
        Bits.putInt(b, 1, Client.frozenGametick);
        b[5] = Settings.NET_UDP_CMD_ACK_MOVE;
        udpSend(b);
    }

    /**
     * Bestätigt dem Server den erhalt dieses Chars
     *
     * @param netID
     */
    private void ackNewChar(int netID) {
        byte[] b = new byte[10];
        b[0] = Client.getClientID();
        Bits.putInt(b, 1, Client.frozenGametick);
        b[5] = Settings.NET_UDP_CMD_ACK_ADD_ENTITY;
        Bits.putInt(b, 6, netID);
        udpSend(b);
    }

    /**
     * Bestätigt dem Server den erhalt des Löschens dieses Chars
     *
     * @param netID
     */
    private void ackDelChar(int netID) {
        byte[] b = new byte[10];
        b[0] = Client.getClientID();
        Bits.putInt(b, 1, Client.frozenGametick);
        b[5] = Settings.NET_UDP_CMD_ACK_DEL_ENTITY;
        Bits.putInt(b, 6, netID);
        udpSend(b);
    }

    /**
     * Sendet ein packet per UDP an den Server
     *
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
