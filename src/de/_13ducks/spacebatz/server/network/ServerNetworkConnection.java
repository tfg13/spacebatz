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
     * der puffer für eingehende nachrichten
     */
    private byte buffer[];
    /**
     * die zahl der bytes die noch gelesen wird für das aktuelle packet
     */
    private int bytesToRead;

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
            if (bytesToRead == 0) {
                if (mySocket.getInputStream().available() > 0) {
                    bytesToRead = mySocket.getInputStream().read();
                    buffer = new byte[bytesToRead];
                }
            } else {
                if (mySocket.getInputStream().available() >= bytesToRead) {
                    for (int i = 0; i < bytesToRead; i++) {
                        buffer[i] = (byte) mySocket.getInputStream().read();
                    }
                    Server.msgInterpreter.interpretTCPMessage(buffer, myClient);
                    bytesToRead = 0;
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
