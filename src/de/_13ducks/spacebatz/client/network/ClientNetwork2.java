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
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.Client;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.Constants;
import de._13ducks.spacebatz.shared.network.MessageFragmenter;
import de._13ducks.spacebatz.shared.network.OutBuffer;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.shared.network.Utilities;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Die Client-Seite des neuen Netzwerksystems
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientNetwork2 {

    /**
     * Sagt ob wir zu einem Server verbunden sind, und daher Daten senden und empfangen können.
     */
    private boolean connected = false;
    /**
     * Adresse des Servers. Nur definiert, wenn connected = true
     */
    private InetAddress serverAdr;
    /**
     * Port des Servers. Nur definiert, wenn connected = true
     */
    private int serverPort;
    /**
     * Thread, der auf UDP-Pakete lauscht.
     */
    private Thread thread;
    /**
     * Das verwendete Socket.
     */
    private DatagramSocket socket;
    /**
     * Die Nummer des letzten vom Server empfangenen Netzwerkpakets.
     */
    private short lastInIndex;
    /**
     * Die Nummer des nächsten zu versendenen Pakets.
     */
    private int nextOutIndex = 1;
    /**
     * Die Queue der ankommenden Pakete.
     */
    private PriorityBlockingQueue<STCPacket> inputQueue = new PriorityBlockingQueue<>();
    /**
     * Die zweite Queue der ankommenden Pakete. Der Wrap-Around funktioniert also nicht
     */
    private PriorityBlockingQueue<STCPacket> inputQueue2 = new PriorityBlockingQueue<>();
    /**
     * Enthält alle bekannten Netzkommandos, die der Server ausführen kann. Enthält sowohl interne, als auch externe Kommandos.
     */
    static STCCommand[] cmdMap = new STCCommand[256];
    /**
     * Puffert Befehle, die gesendet werden sollen.
     */
    private Queue<OutgoingCommand> cmdOutQueue = new LinkedBlockingQueue<>();
    /**
     * Puffert Pakete, die gesendet werden sollen.
     */
    OutBuffer outBuffer = new OutBuffer();
    /**
     * Timer zählt Serverticks so weit wie möglich mit, um Lerp reinrechnen zu können.
     */
    private Timer lerpTimer = new Timer("NET_LERPTIMER", true);
    /**
     * Der mehr oder weniger aktuelle Tickwert des Servers. Ist zwangsweise mindestens um die Ping der Sync-Pakete verschoben.
     */
    private int serverTick;
    /**
     * Gibt an, bei welchem Servertick das Paket mit der packID 0 gesendet wurde. Dieser Wert ist notwendig, um bei der Lerp-Berechnung herausfinden zu können, bei welchem Tick ein Paket vom Server
     * gesendet wurde. Dann kann einfach bestimmt werden, wie lange das Paket noch zurückgehalten werden soll. Wert wird bei jedem Wrap-Around der packIDs neu manipuliert.
     */
    private int packZeroServerTick;
    /**
     * Die Anzahl von Ticks, die der Client absichtlicht hinter dem Server gehalten wird. Der hier initialisierte Wert ist default, kann aber im laufenden Spiel (automatisch) angepasst werden. Der
     * Defaultwert von 10 entspricht 150 ms. Das ist etwa der minimale Wert, den OutBuffer.MAX_ACKTIME_MS noch zulässt. Wenn dieser Wert geändert wird, sollte auch OutBuffer.MAX_ACKTIME_MS geändert
     * werden.
     *
     * @todo: änderbar machen.
     */
    private int lerp = 10;

    /**
     * Erzeugt ein neues Netzwerkmodul.
     */
    public ClientNetwork2() {
        cmdMap[0x80] = new STC_ACK();
        cmdMap[Settings.NET_FRAGMENTED_MESSAGE] = new STC_FRAGMENTED_MESSAGE(); // 0x81
    }

    /**
     * Weist das Netzwerksystem an, sich zur Zieladdresse/Port zu verbinden. Liefert true bei erfolgreichem Verbindungsaufbau. Anschließend ist der Setup des Netzwerksystems abgeschlossen und es kann
     * verwendet werden. Wenn false returned wurde, kann diese Methode für weitere Versuche erneut aufgerufen werden. Diese Methode blockt, bis der Server geantwortet hat. (Maximal 5 Sekunden)
     *
     * @param targetAddress die Zieladdresse
     * @param port der Ziel-Port
     * @return true, wenn erfolgreich, sonst false
     */
    public synchronized void connect(final InetAddress targetAddress, final int port) {
        // Vorerst mal einen neuen Thread starten, sollte später nichtmehr möglich sein
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new DatagramSocket();
                } catch (SocketException ex) {
                    return;// false;
                }
                try {
                    // Initialize-Paket an den Server schicken:
                    byte[] data = new byte[5];
                    Bits.putInt(data, 1, socket.getLocalPort());
                    data[0] = (byte) (1 << 6); // NETMODE auf noClient, connect
                    DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, port);
                    // Antwort-Packet
                    byte[] ansData = new byte[8];
                    DatagramPacket ansPacket = new DatagramPacket(ansData, ansData.length);
                    socket.setSoTimeout(50000);
                    socket.send(packet);
                    while (true) {
                        try {
                            socket.receive(ansPacket);
                            socket.setSoTimeout(0);
                        } catch (SocketTimeoutException timeoutEx) {
                            // Timeout, ging nicht, Ende.
                            socket.close();
                            System.out.println("Connecting failed. Request timed out.");
                            return;// false;
                        }
                        // Antwort auswerten (via netmode):
                        if ((ansData[0] & 0xC0) == 0x40) {
                            socket.setSoTimeout(0);
                            // Verbindung ok, Parameter auslesen.
                            ansData[0] &= 0x3F;
                            int packID = Bits.getShort(ansData, 0);
                            int clientID = ansData[2];
                            serverTick = Bits.getInt(ansData, 3);
                            lerpTimer.scheduleAtFixedRate(new TimerTask() {
                                @Override
                                public void run() {
                                    serverTick++;
                                }
                            }, ansData[7], ansData[7]);
                            lastInIndex = (short) (packID - 1);
                            if (lastInIndex < 0) {
                                lastInIndex = (short) de._13ducks.spacebatz.shared.network.Constants.OVERFLOW_STC_PACK_ID - 1;
                            }
                            packZeroServerTick = serverTick - packID;
                            serverAdr = targetAddress;
                            serverPort = port;
                            connected = true;
                            System.out.println("INFO: NET: Connection established. ClientID " + clientID + ", nextPackID " + packID);
                            initializeReceiver();
                            return;// true;
                        } else if ((ansData[0] & 0xC0) == 0x80) {
                            System.out.println("Connecting failed. Server rejected request. Reason: " + (ansData[0] & 0x3F));
                            socket.close();
                            return;// false;
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Connecting failed. IOException: " + ex.getLocalizedMessage() + " reason: " + ex.getCause());
                    socket.close();
                    return;// false;
                }
            }
        });
        t.setName("NETNET_CONNECT_HELP");
        t.setDaemon(true);
        t.start();
    }

    private void initializeReceiver() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        DatagramPacket pack = new DatagramPacket(new byte[512], 512);
                        // blockt
                        socket.receive(pack);
                        byte[] data = Utilities.extractData(pack);
                        byte mode = data[0];
                        // NETMODE auswerten:
                        switch (mode >>> 6) {
                            case 0:
                                // Normales Datenpaket
                                enqueuePacket(new STCPacket(data));
                                break;
                            default:
                                System.out.println("WARNING: NET: Ignoring packet with unknown netmode (" + (mode >>> 6) + ")");
                                break;
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("ERROR: NET: Receiving data failed. Details:");
                    ex.printStackTrace();
                }
            }
        }, "CNET_RECEIVE");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Queued ein angekommendes Paket, falls relevant und nicht bereits vorhanden.
     *
     * @param packet das neue Paket.
     */
    private synchronized void enqueuePacket(STCPacket packet) {
        // Nicht aufnehmen, wenn zu alt (wrap-around)
        int packdiff = Math.abs(packet.getIndex() - lastInIndex);
        if ((packet.getIndex() < lastInIndex && packdiff > Constants.MAX_WRAPAROUND_PACK_ID_DIFF) || (packet.getIndex() > lastInIndex && packdiff < Constants.MAX_WRAPAROUND_PACK_ID_DIFF)) {
            // Sonderbehandlung für Wrap-Around in zweite Queue
            if (packet.getIndex() < lastInIndex) {
                if (!inputQueue2.contains(packet)) {
                    inputQueue2.add(packet);
                }
            } else {
                if (!inputQueue.contains(packet)) {
                    inputQueue.add(packet);
                }
            }
        }
        // Empfang immer bestätigen:
        ackPacket(packet);
    }

    /**
     * Craftet ein ACK-Signal und scheduled es zum Senden
     *
     * @param packet das Empfangene STCPacket
     */
    private void ackPacket(STCPacket packet) {
        byte[] ackData = new byte[2];
        Bits.putShort(ackData, 0, packet.getIndex());
        queueOutgoingCommand(new OutgoingCommand(0x80, ackData));
    }

    /**
     * Schiebt einen Befehl in die Warteschlange für ausgehende Befehle.
     *
     * @param cmd
     */
    public void queueOutgoingCommand(OutgoingCommand cmd) {
        if (cmd.data.length > 128) {
            byte[][] fragments = MessageFragmenter.fragmentMessage((byte) cmd.cmdID, cmd.data);
            for (int i = 0; i < fragments.length; i++) {
                cmdOutQueue.add(new OutgoingCommand(Settings.NET_FRAGMENTED_MESSAGE, fragments[i]));
            }
        } else {
            cmdOutQueue.add(cmd);
        }

    }

    /**
     * Baut aus den Befehlen, die derzeit in der Warteschlange sind ein Netzwerkpaket zusammen.
     *
     * @return das DatenPaket
     */
    DatagramPacket craftPacket() {
        byte[] buf = new byte[512];
        buf[0] = Client.getClientID();
        short idx = getAndIncrementNextIndex();
        Bits.putShort(buf, 1, idx);
        buf[3] = 0; // MAC
        int pos = 4;
        while (!cmdOutQueue.isEmpty() && cmdOutQueue.peek().data.length + 1 <= 511 - pos) {
            // Befehl passt noch rein
            OutgoingCommand cmd = cmdOutQueue.poll();
            buf[pos++] = (byte) cmd.cmdID;
            System.arraycopy(cmd.data, 0, buf, pos, cmd.data.length);
            pos += cmd.data.length;
        }
        if (pos == 4) {
            // NOOP einbauen
            buf[4] = 0;
        }
        return new DatagramPacket(buf, pos + 1, serverAdr, serverPort);
    }

    private short getAndIncrementNextIndex() {
        short ret = (short) nextOutIndex++;
        if (nextOutIndex == Constants.OVERFLOW_STC_PACK_ID) {
            nextOutIndex = 0;
        }
        return ret;
    }

    /**
     * Registriert einen neuen Befehl beim Netzwerksystem. Zukünfig werden empfangene Kommandos, die die angegebene ID haben von dem gegebenen Kommando bearbeitet. Die gewählte ID muss im erlaubten
     * Bereich für externe Befehle liegen (siehe Netzwerk-Dokumentation)
     *
     * @param cmdID die BefehlsID
     * @param cmd der Befehl selber
     */
    public void registerSTCCommand(byte cmdID, STCCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("STCCommand must not be null!");
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
        System.out.println("INFO: NET: Registered STC cmd " + cmdID);
    }

    /**
     * Muss zu Anfang jedes Ticks aufgerufen werden, verarbeitet Befehle vom Server. Darf erst nach connect aufgerufen werden.
     */
    public synchronized void inTick() {
        if (connected) {
            // Schauen, ob der Index des nächsten Pakets stimmt:
            while (true) {
                // Queues tauschen?
                if (inputQueue.isEmpty() && !inputQueue2.isEmpty() && lastInIndex == Constants.OVERFLOW_STC_PACK_ID - 1) {
                    System.out.println("CLIENT QUEUE SWAP");
                    PriorityBlockingQueue<STCPacket> temp = inputQueue;
                    inputQueue = inputQueue2;
                    inputQueue2 = temp;
                    // Servertick bei packID 0 ändern:
                    packZeroServerTick += Constants.OVERFLOW_STC_PACK_ID;
                }
                if (inputQueue.isEmpty()) {
                    break;
                }
                // Berechnen, bei welchem Servertick das nächste Paket gesendet wurde:
                int packetServerTick = inputQueue.peek().getIndex() + packZeroServerTick;
                int next = lastInIndex + 1;
                if (next == Constants.OVERFLOW_STC_PACK_ID) {
                    next = 0;
                }
                // Zweite Bedingung ist Lerp. Verzögern, bis wir lerp Ticks Vorsprung haben
                if (inputQueue.peek().getIndex() == next && packetServerTick < serverTick + lerp) {
                    STCPacket packet = inputQueue.poll();
                    packet.compute();
                    lastInIndex = packet.getIndex();
                } else {
                    break;
                }
            }
        } else {
            System.out.println("ERROR: Cannot read inputData, not connected!");
        }
    }

    /**
     * Muss zu Ende jedes Ticks aufgerufen werden, sendet Daten an den Server. Darf erst nach connect aufgerufen werden.
     */
    public void outTick() {
        if (connected) {
            try {
                DatagramPacket dPack = craftPacket();
                schedulePacket(dPack, Bits.getShort(dPack.getData(), 1));
                ArrayList<DatagramPacket> sendList = outBuffer.packetsToSend();
                for (DatagramPacket packet : sendList) {
                    socket.send(packet);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("ERROR: Cannot send data, not connected!");
        }
    }

    /**
     * Registriert dieses Paket. Das bedeutet, dass der Server dieses Paket erhalten soll. Das Netzwerksystem wird dieses Paket so lange zwischenspeichern und ggf. neu senden, bis der Server den
     * Empfang bestätigt hat.
     *
     * @param dPack Ein Netzwerkpaket
     * @param packID die PaketID
     */
    private void schedulePacket(DatagramPacket dPack, int packID) {
        if (!outBuffer.registerPacket(dPack, packID)) {
            // Es ist hoffnungslos
            System.out.println("ERROR: CNET: Paket output overflow!!!");
        }
    }
}
