package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import java.io.IOException;
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

            tcpReceiverStatus = RECEIVE_CMDID;

            while (true) {
                if (tcpReceiverStatus == RECEIVE_CMDID) {
                    if (mySocket.getInputStream().available() > 0) {
                        cmdId = (byte) mySocket.getInputStream().read();
                        tcpReceiverStatus = RECEIVE_PACKETSIZE;
                    }
                } else if (tcpReceiverStatus == RECEIVE_PACKETSIZE) {
                    if (mySocket.getInputStream().available() > 1) {
                        int blocks = mySocket.getInputStream().read();
                        int rest = mySocket.getInputStream().read();
                        messageSize = blocks * 100 + rest;
                        tcpReceiverStatus = RECEIVE_PACKET;
                    }
                } else if (tcpReceiverStatus == RECEIVE_PACKET) {
                    if (mySocket.getInputStream().available() > messageSize) {
                        for (int i = 0; i < messageSize; i++) {
                            buffer[i] = (byte) mySocket.getInputStream().read();
                        }
                        Server.msgInterpreter.interpretTCPMessage(buffer, myClient);
                        tcpReceiverStatus = RECEIVE_CMDID;
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
}
