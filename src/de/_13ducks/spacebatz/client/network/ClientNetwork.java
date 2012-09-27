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
import de._13ducks.spacebatz.client.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

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
     * Die Adresse des Servers.
     */
    private InetAddress serverAdr;

    /**
     * Konstruktor
     */
    public ClientNetwork() {
	mySocket = new Socket();
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
	    serverAdr = serverAddress.getAddress();

	    GameClient.getNetwork2().connect(serverAdr, Settings.SERVER_UDPPORT2);

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
			//Client.getMsgInterpreter().addMessageToQueue(new ClientTcpMessage(cmdId, data));
		    }
		} catch (IOException ex) {
		    ex.printStackTrace();

		}
	    }
	});
	receiveDataThread.setName("ReceiveDataThread");
	receiveDataThread.setDaemon(true);
	receiveDataThread.start();
    }

    /**
     * Liefert die Serveradresse, zu der wir verbunden sind.
     * Das Verhalten, wenn noch nicht connected ist nicht definiert.
     *
     * @return die Serveradresse
     */
    public InetAddress getServerAdr() {
	return serverAdr;
    }
}
