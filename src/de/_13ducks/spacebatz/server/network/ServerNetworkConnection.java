package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
     * Konstruktor, erstellt eine neue NetworkCOnnection zu einem Client.
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
}
