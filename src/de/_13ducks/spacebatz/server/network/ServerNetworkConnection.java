package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.client.network.ClientTcpMessage;
import de._13ducks.spacebatz.util.Bits;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Verbindung mit einem Client Verwaltet den Socket und das einlesen/schreiben von Daten
 *
 * @author michael
 */
public class ServerNetworkConnection {

    /**
     * Der Socket, der mit dem Client verbunden ist
     */
    private Socket mySocket;
    /**
     * Sendet bytes, longs etc.
     */
    private ObjectOutputStream sendStream;
    /**
     * Der Stream zum empfangen von DAten
     */
    private ObjectInputStream receiveStream;
    /**
     * Der Client der zu dieser Verbindung gehört
     */
    private Client myClient;
    /**
     * Der Status des TCP-Empfangsthreads (cmdId empfangen, PacketLänge empfangen oder DAten empfangen)
     */
    private int tcpReceiverStatus;
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
     * Konstruktor, erstellt einen neuen Client
     *
     * @param socket der Socket, der mit dem Client verbunden ist
     */
    public ServerNetworkConnection(Socket socket) {
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
     * Empfängt Daten vom Client. Wenn genug Daten für ein Packet da sind wird das Packet an den MessageInterpreter weitergeleitet.
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
     * gibt den zugehörigen Client zurück
     *
     * @return der Client
     */
    public Client getClient() {
        return myClient;
    }

    /**
     * Gibt den Sendstream zurück, mit dem DAten gesendet werden können
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
}
