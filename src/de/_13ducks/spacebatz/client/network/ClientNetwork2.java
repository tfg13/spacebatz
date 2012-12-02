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
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.shared.network.Constants;
import de._13ducks.spacebatz.shared.network.MessageFragmenter;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutBuffer;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.shared.network.Utilities;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_BROADCAST_GROUND_CHANGE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_COLLISION;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_LEVEL;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_MATERIAL_AMOUNT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_INV_ITEM_MOVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DROP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TOGGLE_ALIVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CLIENT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_PLAYER;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_SKILL_MAPPING;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_START_ENGINE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_ENEMYTYPES;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_UPDATE_SKILLTREE;
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
     * Puffert Befehle, die mit höherer Priorität gesendet werden sollen.
     */
    private Queue<OutgoingCommand> priorityCmdOutQueue = new LinkedBlockingQueue<>();
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
     * Der Zeitpunkt, zu dem der letzte Ping-Request an den Server geschickt wurde.
     */
    private long lastPingOut;
    /**
     * Die letzte gemessene Netzwerkauslastung.
     * Ein ganzzahliger Prozentwert
     */
    private int lastLoad;

    /**
     * Erzeugt ein neues Netzwerkmodul.
     */
    public ClientNetwork2() {
        cmdMap[0x80] = new STC_ACK();
        cmdMap[0x88] = new STC_MULTI();
        cmdMap[MessageIDs.NET_FRAGMENTED_MESSAGE] = new STC_FRAGMENTED_MESSAGE(); // 0x81
        cmdMap[MessageIDs.NET_ENTITY_UPDATE] = new STC_ENTITY_UPDATE(); // 0x83
        cmdMap[MessageIDs.NET_ENTITY_CREATE] = new STC_ENTITY_CREATE(); // 0x84
        cmdMap[MessageIDs.NET_ENTITY_REMOVE] = new STC_ENTITY_REMOVE(); // 0x85
        cmdMap[MessageIDs.NET_TRANSFER_CHUNK] = new STC_TRANSFER_CHUNK(); // 0x86
        registerSTCCommand(MessageIDs.NET_TCP_CMD_CHAR_HIT, new STC_CHAR_HIT());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_EQUIP_ITEM, new STC_EQUIP_ITEM());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_DEQUIP_ITEM, new STC_ITEM_DEQUIP());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_CHANGE_GROUND, new STC_BROADCAST_GROUND_CHANGE());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_CHANGE_COLLISION, new STC_CHANGE_COLLISION());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_SWITCH_WEAPON, new STC_SWITCH_WEAPON());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_SPAWN_ITEM, new STC_ITEM_DROP());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_TRANSFER_ENEMYTYPES, new STC_TRANSFER_ENEMYTYPES());
        registerSTCCommand(MessageIDs.NET_STC_SET_CLIENT, new STC_SET_CLIENT());
        registerSTCCommand(MessageIDs.NET_STC_SET_PLAYER, new STC_SET_PLAYER());
        registerSTCCommand(MessageIDs.NET_STC_START_ENGINE, new STC_START_ENGINE());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_ANSWER_RCON, new STC_ANSWER_RCON());
        registerSTCCommand(MessageIDs.NET_CHANGE_LEVEL, new STC_CHANGE_LEVEL());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_CHANGE_MATERIAL_AMOUNT, new STC_CHANGE_MATERIAL_AMOUNT());
        registerSTCCommand(MessageIDs.NET_STC_UPDATE_SKILLTREE, new STC_UPDATE_SKILLTREE());
        registerSTCCommand(MessageIDs.NET_STC_SET_SKILL_MAPPING, new STC_SET_SKILL_MAPPING());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_INV_ITEM_MOVE, new STC_INV_ITEM_MOVE());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_CHAR_ATTACK, new STC_CHAR_ATTACK());
        registerSTCCommand(MessageIDs.NET_TCP_CMD_PLAYER_TOGGLE_ALIVE, new STC_PLAYER_TOGGLE_ALIVE());
    }

    /**
     * Weist das Netzwerksystem an, sich zur Zieladdresse/Port zu verbinden. Liefert true bei erfolgreichem Verbindungsaufbau. Anschließend ist der Setup des Netzwerksystems abgeschlossen und es kann
     * verwendet werden. Wenn false returned wurde, kann diese Methode für weitere Versuche erneut aufgerufen werden. Diese Methode blockt, bis der Server geantwortet hat. (Maximal 1 Sekunde)
     *
     * @param targetAddress die Zieladdresse
     * @param port der Ziel-Port
     * @return true, wenn erfolgreich, sonst false
     */
    public synchronized boolean connect(final InetAddress targetAddress, final int port) {
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            return false;
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
            socket.setSoTimeout(1000);
            socket.send(packet);
            while (true) {
                try {
                    socket.receive(ansPacket);
                    socket.setSoTimeout(0);
                } catch (SocketTimeoutException timeoutEx) {
                    // Timeout, ging nicht, Ende.
                    socket.close();
                    System.out.println("Connecting failed. Request timed out.");
                    return false;
                }
                // Antwort auswerten (via netmode):
                if ((ansData[0] & 0xC0) == 0x40) {
                    socket.setSoTimeout(0);
                    // Verbindung ok, Parameter auslesen.
                    ansData[0] &= 0x3F;
                    int packID = Bits.getShort(ansData, 0);
                    byte clientID = ansData[2];
                    GameClient.setClientID(clientID);
                    serverTick = Bits.getInt(ansData, 3);
                    GameClient.setLogicTick(serverTick);
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
                    return true;
                } else if ((ansData[0] & 0xC0) == 0x80) {
                    System.out.println("Connecting failed. Server rejected request. Reason: " + (ansData[0] & 0x3F));
                    socket.close();
                    return false;
                }
            }
        } catch (IOException ex) {
            System.out.println("Connecting failed. IOException: " + ex.getLocalizedMessage() + " reason: " + ex.getCause());
            socket.close();
            return false;
        }
    }

    /**
     * Gibt das Kommando für diese ID zurück
     *
     * @param id
     * @return
     */
    STCCommand getCmdForId(int id) {
        return cmdMap[id];
    }

    private void initializeReceiver() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        DatagramPacket pack = new DatagramPacket(new byte[1460], 1460);
                        // blockt
                        socket.receive(pack);
                        byte[] data = Utilities.extractData(pack);
                        int mode = data[0] & 0xFF;
                        // NETMODE auswerten:
                        switch (mode >>> 6) {
                            case 0:
                                // Normales Datenpaket
                                STCPacket stc = new STCPacket(data);
                                stc.preCompute();
                                enqueuePacket(stc);
                                break;
                            case 2:
                                // Realtime
                                // Mode?
                                int rtMode = mode & 0x3F;
                                switch (rtMode) {
                                    case 0:
                                        // Ping-Messung abgeschlossen:
                                        NetStats.ping = (int) (System.currentTimeMillis() - lastPingOut);
                                        lastPingOut = 0;
                                        break;
                                    default:
                                        System.out.println("WARNING: CNET: Packet with unknown RT-Mode (" + rtMode + ")");
                                        break;
                                }
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
        if (cmd.cmdID == 0x80) {
            // Wenn es ein ACK ist in die Prioritätsqueue packen
            priorityCmdOutQueue.add(cmd);
        } else {
            // "Normale" Packete in die normale Queue
            if (cmd.data.length > 128) {
                byte[][] fragments = MessageFragmenter.fragmentMessage((byte) cmd.cmdID, cmd.data);
                for (int i = 0; i < fragments.length; i++) {
                    cmdOutQueue.add(new OutgoingCommand(MessageIDs.NET_FRAGMENTED_MESSAGE, fragments[i]));
                }
            } else {
                cmdOutQueue.add(cmd);
            }
        }
    }

    /**
     * Baut aus den Befehlen, die derzeit in der Warteschlange sind ein Netzwerkpaket zusammen.
     *
     * @return das DatenPaket
     */
    DatagramPacket craftPacket() {
        byte[] buf = new byte[1460];
        buf[0] = GameClient.getClientID();
        short idx = getAndIncrementNextIndex();
        Bits.putShort(buf, 1, idx);
        buf[3] = 0; // MAC
        int pos = 4;
        // Erst Packete mit höherer Priorität verwenden:
        while (!priorityCmdOutQueue.isEmpty() && priorityCmdOutQueue.peek().data.length + 1 <= 511 - pos) {
            // Befehl passt noch rein
            OutgoingCommand cmd = priorityCmdOutQueue.poll();
            buf[pos++] = (byte) cmd.cmdID;
            System.arraycopy(cmd.data, 0, buf, pos, cmd.data.length);
            pos += cmd.data.length;
        }
        // Dann normale Packete:
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
    public final void registerSTCCommand(byte cmdID, STCCommand cmd) {
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
                if (inputQueue.isEmpty() && !inputQueue2.isEmpty() && lastInIndex == -1) {
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
                // Zweite Bedingung ist Lerp. Verzögern, bis wir lerp Ticks Vorsprung haben
                if (inputQueue.peek().getIndex() == next && packetServerTick <= serverTick - lerp) {
                    STCPacket packet = inputQueue.poll();
                    lastLoad = (int) (100.0 * packet.getDataLength() / 1460);
                    packet.compute();
                    lastInIndex = packet.getIndex();
                    if (lastInIndex == Constants.OVERFLOW_STC_PACK_ID - 1) {
                        lastInIndex = -1;
                    }
                    if (packet.isMulti()) {
                        packZeroServerTick--;
                    }
                } else {
                    break;
                }
            }
        } else {
            //System.out.println("ERROR: Cannot read inputData, not connected!");
        }
    }

    /**
     * Muss zu Ende jedes Ticks aufgerufen werden, sendet Daten an den Server. Darf erst nach connect aufgerufen werden.
     */
    public void outTick() {
        if (connected) {
            // Bis zu ein Mal pro Sekunde Ping messen:
            if (serverTick % (1000 / Settings.SERVER_TICKRATE) == 0 && lastPingOut == 0) {
                try {
                    byte[] pingData = new byte[2];
                    pingData[0] = (byte) 0x80;
                    pingData[1] = GameClient.getClientID();
                    DatagramPacket pingPacket = new DatagramPacket(pingData, pingData.length, serverAdr, serverPort);
                    lastPingOut = System.currentTimeMillis();
                    socket.send(pingPacket);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
            //System.out.println("ERROR: Cannot send data, not connected!");
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
            // Verbindung trennen
            connected = false;
        }
    }

    /**
     * Liefert den aktuellen Logik-Tick zurück - also den geschätzten Tickwert der Servers.
     * Achtung: Hier wurde bereits Lerp reingerechnet!
     *
     * @return
     */
    public int getLogicTick() {
        return serverTick - lerp;
    }

    /**
     * Liefert die Adresse des Servers
     *
     * @return die Adresse des Servers
     */
    public InetAddress getServerAdr() {
        return serverAdr;
    }

    /**
     * Findet heraus, ob die Verbindung steht.
     * Wenn das false ist, ist die Verbindung entweder noch nicht hergestellt, oder schon wieder kaputt.
     *
     * @return true, wenn connected.
     */
    public boolean connectionAlive() {
        return connected;
    }

    /**
     * Liefert die "Gesundheit" der Connection.
     * Dieser Wert sollte 100 (%) sein, sonst ist die Verbindung am kaputtgehen.
     * Nicht definiert, wenn connectionAlive() == false
     *
     * @return Gesundheit in Prozent
     */
    public int getConnectionHealthPercent() {
        return 100 - outBuffer.getBufferRatio();
    }

    /**
     * Liefert die Auslastung des Netzwerksystems, in Prozent.
     * Ist dieser Wert mehrere Ticks lang sehr hoch, so kommt es zu lags.
     * Nicht definiert, wenn connectionAlive() == false
     *
     * @return Auslastung in Prozent.
     */
    public int getConnectionLoadPercent() {
        return lastLoad;
    }

    /**
     * Liefert den aktuellen Lerp-Wert zurück
     *
     * @return den akteullen Lerp-Wert
     */
    public int getLerp() {
        return lerp;
    }

    /**
     * Setzt einen neuen Lerp-Wert. (in Ticks)
     * Derzeit nur per Terminal erreichbar und auch dort mit Vorsicht zu genießen.
     * Eine Änderungen dieses Wertes wird im Normalfall von kurzen Rucklern/Sprüngen begleitet, bis die Pakete aufgeholt / die Puffer gefüllt sind.
     * Negative Werte sind nicht explizit verboten, sollten aber keinerlei Sinn machen.
     *
     * @param lerp der neue Lerp-Wert
     */
    void setLerp(int lerp) {
        System.out.println("INFO: NET: Changed lerp to " + lerp + " upon user request");
        this.lerp = lerp;
    }

    /**
     * Beendet die Verbindung zum Server auf normale Art und Weise.
     * Dieses Paket wird besonders priorisiert und kann noch vor alten Datenpaketen ankommen und diese damit verwerfen lassen!
     */
    public void disconnect() {
        try {
            byte[] dcData = new byte[2];
            dcData[0] = (byte) 0x81;
            dcData[1] = GameClient.getClientID();
            DatagramPacket dcPacket = new DatagramPacket(dcData, dcData.length, serverAdr, serverPort);
            socket.send(dcPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
