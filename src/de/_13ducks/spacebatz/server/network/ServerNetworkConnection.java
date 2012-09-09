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

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.Constants;
import de._13ducks.spacebatz.shared.network.OutBuffer;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.Socket;
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
     * Der Socket, der mit dem Client verbunden ist
     */
    private Socket mySocket;
    /**
     * Der Stream zum Senden von Daten
     */
    private ObjectOutputStream sendStream;
    /**
     * Der Stream zum empfangen von Daten
     */
    private ObjectInputStream receiveStream;
    /**
     * Der Client der zu dieser Verbindung gehört
     */
    private Client myClient;
    /**
     * Die bytes die für die aktuelle Message noch gelesen werden müssen
     */
    private short messageSize;
    /**
     * Die cmdId der message, die gerade empfangen wird
     */
    private byte cmdId;
    /**
     * Der Puffer, in den die Daten der aktuellen Message gelesen werden
     */
    private byte buffer[];
    /**
     * Die Zahl der bytes, die bereits in den Puffer gelesen wurden
     */
    private int index = 0;
    /**
     * Der Status des TCP-Empfangsthreads (cmdId empfangen, PacketLänge empfangen oder Daten empfangen)
     *
     * Gültige Werte sind RECEIVE_CMDID, RECEIVE_PACKETSIZE und RECEIVE_PACKET
     */
    private int tcpReceiverStatus;
    /**
     * 'Status: cmdId empfangen
     */
    final static int RECEIVE_CMDID = 0;
    /**
     * Status: Packetgröße empfangen
     */
    final static int RECEIVE_PACKETSIZE = 1;
    /**
     * Status: PacketDaten empfangen
     */
    final static int RECEIVE_PACKET = 2;
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
     * Der Index des Datenpakets, dass der Server als nächstes versendet.
     */
    int nextOutIndex = 0x3FF0;

    /**
     * Konstruktor, erstellt eine neue NetworkCOnnection zu einem Client.
     *
     * @param socket der Socket, der mit dem Client verbunden ist
     */
    public ServerNetworkConnection(Socket socket) {
	inputQueue = new PriorityBlockingQueue<>();
	inputQueue2 = new PriorityBlockingQueue<>();
	mySocket = socket;
	tcpReceiverStatus = RECEIVE_CMDID;
	try {
	    sendStream = new ObjectOutputStream(socket.getOutputStream());
	    receiveStream = new ObjectInputStream(socket.getInputStream());
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * Empfängt Daten vom Client.
     *
     * Liest alle empfangenen Daten ein. Wenn genug Daten für ein Packet da sind wird eine neue ServerTcpMessage erstellt und an den MessageInterpreter weitergeleitet.
     */
    public void receiveData() {

	try {
	    while (receiveStream.available() > 0) {
		switch (tcpReceiverStatus) {
		    case RECEIVE_CMDID:
			if (receiveStream.available() > 0) {
			    cmdId = receiveStream.readByte();
			    tcpReceiverStatus = RECEIVE_PACKETSIZE;
			} else {
			    return;
			}
			break;
		    case RECEIVE_PACKETSIZE:
			if (receiveStream.available() > 1) {
			    messageSize = receiveStream.readShort();
			    buffer = new byte[messageSize];
			    tcpReceiverStatus = RECEIVE_PACKET;
			} else {
			    return;
			}
			break;
		    case RECEIVE_PACKET:
			if (receiveStream.available() > 0) {
			    index += receiveStream.read(buffer, index, messageSize - index);
			    if (index == messageSize) {
				tcpReceiverStatus = RECEIVE_CMDID;
				Server.msgInterpreter.addTcpMessage(new ServerTcpMessage(cmdId, buffer, myClient));
				buffer = null;
				cmdId = 0;
				index = 0;
			    }

			} else {
			    return;
			}
			break;
		}
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * Gibt den mit dem Client verbundenen Socket zurück
     *
     * @return der Socket
     */
    public Socket getSocket() {
	return mySocket;
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
     * Gibt den Sendstream zurück, mit dem Daten gesendet werden können
     *
     * @return the sendStream
     */
    public ObjectOutputStream getSendStream() {
	return sendStream;
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
		System.out.println("SERVER QUEUE SWAP");
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
     * @param port the port to set
     */
    void setPort(int port) {
	this.port = port;
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
	cmdOutQueue.add(cmd);
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
    DatagramPacket craftPacket() {
	byte[] buf = new byte[512];
	short idx = getAndIncrementNextIndex();
	Bits.putShort(buf, 0, idx);
	buf[2] = 0; // MAC
	int pos = 3;
	while (!cmdOutQueue.isEmpty() && cmdOutQueue.peek().data.length + 1 <= 511 - pos) {
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
	return new DatagramPacket(buf, pos + 1, mySocket.getInetAddress(), port);
    }
}
