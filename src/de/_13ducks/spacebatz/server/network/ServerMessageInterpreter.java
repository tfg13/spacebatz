package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.client.network.ClientTcpMessage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author michael
 */
public class ServerMessageInterpreter {

    /**
     * Queue, die die empfangenen Datenpackete zwischen speichert.
     */
    private ConcurrentLinkedQueue<ClientTcpMessage> messages;

    /**
     * Konstruktor
     */
    public ServerMessageInterpreter() {
        messages = new ConcurrentLinkedQueue<>();
    }

    /**
     * Interpretiert alle Nachrichetn aus dem Puffer
     */
    public void interpretAllTcpMessages() {
        for (int i = 0; i < messages.size(); i++) {
            ClientTcpMessage m = messages.poll();
            interpretTCPMessage(m.getData(), m.getSender());
        }
    }

    /**
     * Schiebt eine neue Tcp-Nachritch in den Puffer
     */
    public void addTcpMessage(ClientTcpMessage message) {
        messages.add(message);
    }

    /**
     * Interpretiert eine TCP Nachricht von einem Client.
     * @param message die Nachricht als byte-array
     */
    public void interpretTCPMessage(byte message[], Client sender) {
        System.out.print("TCP received: ");
        for (int i = 0; i < message.length; i++) {
            System.out.print((int) message[i]);
        }
        System.out.print("\n");

        //TODO: Nachricht interpretieren...
    }
}
