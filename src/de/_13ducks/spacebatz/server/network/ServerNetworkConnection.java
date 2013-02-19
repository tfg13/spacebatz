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
package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.Constants;
import de._13ducks.spacebatz.shared.network.MessageFragmenter;
import de._13ducks.spacebatz.shared.network.OutBuffer;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Eine Verbindung mit einem Client. Verwaltet den Socket und das Senden/Empfangen von Daten mit diesem Client.
 *
 * @author michael
 */
public class ServerNetworkConnection {

    /**
     * Defragentiert Nachrichten
     */
    private MessageFragmenter fragmenter;
    /**
     * Der Client der zu dieser Verbindung gehört
     */
    private Client myClient;
    /**
     * Die Adresse dieses Clients.
     */
    private InetAddress clientAddress;
    /**
     * Die Nummer des letzten von diesem Client empfangenen Netzwerkpakets.
     */
    private short lastPkgIndex;
    /**
     * Die Queue der ankommenden Pakete.
     */
    private PriorityBlockingQueue<CTSPacket> inputQueue;
    /**
     * Die zweite Queue der ankommenden Pakete.
     * Der Wrap-Around funktioniert also nicht
     */
    private PriorityBlockingQueue<CTSPacket> inputQueue2;
    /**
     * Der Port des neuen Netzwerksystems, auf dem der Client lauscht.
     */
    private int port;
    /**
     * Puffert alle Daten für den Client, bis der sie erhalten hat.
     */
    private OutBuffer outBuffer = new OutBuffer();
    /**
     * Puffert Befehle, die gesendet werden sollen.
     */
    private Queue<OutgoingCommand> cmdOutQueue = new LinkedBlockingQueue<>();
    /**
     * Puffert Befehle, die mit hoher Priorität gesendet werden sollen.
     */
    private Queue<OutgoingCommand> priorityCmdOutQueue = new LinkedBlockingQueue<>();
    /**
     * Der Index des Datenpakets, dass der Server als nächstes versendet.
     * Darf nicht 0 sein, der Client muss noch gefahrlos 1 abziehen können
     * Dieser Index darf nicht größer sein, als die Tickzahl des Servers.
     * Dieser Wert startet also bei 1, so machen auch sehr früh connectende Clients keine Probleme.
     */
    int nextOutIndex = 1;
    /**
     * Der Wissensstand dieses Clients.
     */
    ClientContext2 context = new ClientContext2();

    /**
     * Konstruktor, erstellt eine neue NetworkCOnnection zu einem Client.
     *
     * @param socket der Socket, der mit dem Client verbunden ist
     */
    public ServerNetworkConnection(InetAddress address, int port) {
        fragmenter = new MessageFragmenter();
        inputQueue = new PriorityBlockingQueue<>();
        inputQueue2 = new PriorityBlockingQueue<>();
        clientAddress = address;
        this.port = port;
    }

    /**
     * Gibt den zugehörigen Client zurück
     *
     * @return der Client
     */
    public Client getClient() {
        return myClient;
    }

    /**
     * Setzt den Client
     *
     * @param client
     */
    void setClient(Client client) {
        this.myClient = client;
    }

    /**
     * Queued ein angekommendes Paket, falls relevant und nicht bereits vorhanden.
     *
     * @param packet das neue Paket.
     */
    synchronized void enqueuePacket(CTSPacket packet) {
        // Nicht aufnehmen, wenn zu alt (wrap-around)
        int packdiff = Math.abs(packet.getIndex() - lastPkgIndex);
        if ((packet.getIndex() < lastPkgIndex && packdiff > Constants.MAX_WRAPAROUND_PACK_ID_DIFF) || (packet.getIndex() > lastPkgIndex && packdiff < Constants.MAX_WRAPAROUND_PACK_ID_DIFF)) {
            // Sonderbehandlung für Wrap-Around in zweite Queue
            if (packet.getIndex() < lastPkgIndex) {
                if (!inputQueue2.contains(packet)) {
                    inputQueue2.add(packet);
                }
            } else {
                if (!inputQueue.contains(packet)) {
                    inputQueue.add(packet);
                }
            }
        }
        // Empfang bestätigen:
        ackPacket(packet);
    }

    /**
     * Craftet ein ACK-Signal und scheduled es zum Senden
     *
     * @param packet das Empfangene STCPacket
     */
    private void ackPacket(CTSPacket packet) {
        byte[] ackData = new byte[2];
        Bits.putShort(ackData, 0, packet.getIndex());
        queueOutgoingCommand(new OutgoingCommand(0x80, ackData));
    }

    /**
     * Verarbeitet alle Packete, die derzeit verarbeitet werden können.
     */
    synchronized void computePackets() {
        // Schauen, ob der Index des nächsten Pakets stimmt:
        while (true) {
            // Queues tauschen?
            if (inputQueue.isEmpty() && !inputQueue2.isEmpty() && lastPkgIndex == Constants.OVERFLOW_STC_PACK_ID - 1) {
                PriorityBlockingQueue<CTSPacket> temp = inputQueue;
                inputQueue = inputQueue2;
                inputQueue2 = temp;
            }
            if (inputQueue.isEmpty()) {
                break;
            }
            int next = lastPkgIndex + 1;
            if (next == Constants.OVERFLOW_STC_PACK_ID) {
                next = 0;
            }
            if (inputQueue.peek().getIndex() == next) {
                CTSPacket packet = inputQueue.poll();
                packet.compute();
                lastPkgIndex = packet.getIndex();
            } else {
                break;
            }
        }
    }

    /**
     * @return the port
     */
    int getPort() {
        return port;
    }
    
    /**
     * Liefert die Adresse
     * @return die Adresse
     */
    public InetAddress getInetAddress() {
        return clientAddress;
    }

    /**
     * @return the outBuffer
     */
    OutBuffer getOutBuffer() {
        return outBuffer;
    }

    /**
     * Schiebt einen Befehl in die Warteschlange für ausgehende Befehle.
     *
     * @param cmd
     */
    public void queueOutgoingCommand(OutgoingCommand cmd) {
        if (cmd.cmdID == 0x80) {
            // ACKs in die Prioritätsschlange
            priorityCmdOutQueue.add(cmd);
        } else {
            // Alle anderen in die normale
            cmdOutQueue.add(cmd);
        }

    }

    private short getAndIncrementNextIndex() {
        short ret = (short) nextOutIndex++;
        if (nextOutIndex == Constants.OVERFLOW_STC_PACK_ID) {
            nextOutIndex = 0;
        }
        return ret;
    }

    /**
     * Baut aus den Befehlen, die derzeit in der Warteschlange sind ein Netzwerkpaket zusammen.
     *
     * @return das DatenPaket
     */
    DatagramPacket craftPacket(boolean multi) {
        /*
         * ACHTUNG: NICHT EINFACH SO ANPASSEN, DASS BEI BEDARF MEHRERE PAKETE GESENDET WERDEN!!!
         * Der Clientseitige Lerp-Mechanismus hängt massiv davon ab, dass der Server genau 1 Paket pro Tick versenden.
         * Wenn man mehr als ein neues Paket schickt, muss man ein internes Kommando dazu tun, das dem Client das mitteilt, damit der
         * Lerp entsprechend angepasst werden kann. Das ist noch in keinster Weise eingebaut und muss alles auf einmal gemacht werden.
         */
        byte[] buf = new byte[1460];
        short idx = getAndIncrementNextIndex();
        Bits.putShort(buf, 0, idx);
        buf[2] = 0; // MAC
        int pos = 3;
        // Das Multi-Paket hat ultrahohe Priorität, muss immer als aller erstes kommen:
        if (multi) {
            buf[pos++] = (byte) 0x88;
        }
        // Nachrichten mit hoher Priorität
        while (!priorityCmdOutQueue.isEmpty() && priorityCmdOutQueue.peek().data.length + 1 <= 1459 - pos) {
            // Befehl passt noch rein
            OutgoingCommand cmd = priorityCmdOutQueue.poll();
            buf[pos++] = (byte) cmd.cmdID;
            System.arraycopy(cmd.data, 0, buf, pos, cmd.data.length);
            pos += cmd.data.length;
        }
        // Normale Nachrichten:
        while (!cmdOutQueue.isEmpty() && cmdOutQueue.peek().data.length + 1 <= 1459 - pos) {
            // Befehl passt noch rein
            OutgoingCommand cmd = cmdOutQueue.poll();
            buf[pos++] = (byte) cmd.cmdID;
            System.arraycopy(cmd.data, 0, buf, pos, cmd.data.length);
            pos += cmd.data.length;
        }
        if (pos == 3) {
            // NOOP einbauen
            buf[3] = 0;
        }
        return new DatagramPacket(buf, pos + 1, clientAddress, port);
    }

    /**
     * @return the fragmenter
     */
    public MessageFragmenter getFragmenter() {
        return fragmenter;
    }

    /**
     * Liefert true, wenn noch viele Befehle raus müssen, es also sinnvoll erscheint ein weiteres ("multi")-Paket zu schicken.
     * Wird unmittelbar nach dem leeren der queues durch craftPaket aufgerufen
     * @return true, wenn multi sinnvoll
     */
    boolean qualifiesForMultiPacket() {
        if (!priorityCmdOutQueue.isEmpty()) {
            // Es wurden nicht einmal alle PRIO-Pakete verschickt, es ist also sinnvoll.
            return true;
        }
        if (cmdOutQueue.size() > 2) {
            return true;
        }
        return false;
    }
}
