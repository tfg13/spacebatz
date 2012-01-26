package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.TcpMessage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author michael
 */
public class MessageInterpreter {

    /**
     * Queue, die die empfangenen Datenpackete zwischen speichert.
     */
    private ConcurrentLinkedQueue<TcpMessage> messages;

    /**
     * Konstruktor
     */
    public MessageInterpreter() {
        messages = new ConcurrentLinkedQueue<>();
    }

    /**
     * Interpretiert alle Nachrichetn aus dem Puffer
     */
    public void interpretAllTcpMessages() {
        for (int i = 0; i < messages.size(); i++) {
            TcpMessage m = messages.poll();
            interpretTCPMessage(m.getData(), m.getSender());
        }
    }

    /**
     * Schiebt eine neue Tcp-Nachritch in den Puffer
     */
    public void addTcpMessage(TcpMessage message) {
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
