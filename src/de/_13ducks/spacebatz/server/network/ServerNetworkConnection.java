package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.TcpMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Eine Verbindung mit einem Client
 * Verwaltet den Socket und das einlesen/schreiben von Daten
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
     * Konstruktor, erstellt einen neuen Client
     * 
     * @param socket der Socket, der mit dem Client verbunden ist
     */
    public ServerNetworkConnection(Socket socket) {
        mySocket = socket;
        try {
            sendStream = new ObjectOutputStream(socket.getOutputStream());
            receiveStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Empfängt Daten vom Client.
     * Wenn genug Daten für ein Packet da sind wird das Packet an den MessageInterpreter weitergeleitet.
     */
    public void receiveData() {
        try {
            int messageSize = 0;            // Die bytes die noch gelesen werden müssen
            byte cmdId = 0;                 // Die cmdId
            byte buffer[] = new byte[0];    // der puffer
            int index = 0;                  // der index, bis zu dem der puffer schon gefüllt ist

            tcpReceiverStatus = RECEIVE_CMDID;

            while (true) {
                if (tcpReceiverStatus == RECEIVE_CMDID) {
                    cmdId = receiveStream.readByte();
                    tcpReceiverStatus = RECEIVE_PACKETSIZE;

                } else if (tcpReceiverStatus == RECEIVE_PACKETSIZE) {
                    messageSize = (int) receiveStream.readLong();
                    buffer = new byte[messageSize];
                    tcpReceiverStatus = RECEIVE_PACKET;

                } else if (tcpReceiverStatus == RECEIVE_PACKET) {
                    int read = receiveStream.read(buffer, index, messageSize - index);
                    if (read + index == messageSize) {
                        Server.msgInterpreter.addTcpMessage(new TcpMessage(cmdId, buffer, myClient));
                        tcpReceiverStatus = RECEIVE_CMDID;
                        index = 0;
                        cmdId = 0;
                        messageSize = 0;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Gibt den mit dem Client verbundenen Socket zurück
     * @return der Socket
     */
    public Socket getSocket() {
        return mySocket;
    }

    /**
     * gibt den zugehörigen Client zurück
     * @return der Client
     */
    public Client getClient() {
        return myClient;
    }

    /**
     * Gibt den Sendstream zurück, mit dem DAten gesendet werden können
     * @return the sendStream
     */
    public ObjectOutputStream getSendStream() {
        return sendStream;
    }
}
