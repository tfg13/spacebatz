package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.client.network.ClientTcpMessage;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author michael
 */
public class ServerMessageInterpreter {

    /**
     * Queue, die die empfangenen Datenpackete zwischen speichert.
     */
    private ConcurrentLinkedQueue<ServerTcpMessage> messages;

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
            ServerTcpMessage m = messages.poll();
            interpretTCPMessage(m.getCmdID(), m.getData(), m.getSender());
        }
    }

    /**
     * Schiebt eine neue Tcp-Nachritch in den Puffer
     */
    public void addTcpMessage(ServerTcpMessage message) {
        messages.add(message);
    }

    /**
     * Interpretiert eine TCP Nachricht von einem Client.
     *
     * @param message die Nachricht als byte-array
     */
    public void interpretTCPMessage(byte cmdID, byte message[], Client sender) {
        System.out.print("TCP received: " + cmdID + " , ");
        for (int i = 0; i < message.length; i++) {
            System.out.print((int) message[i]);
        }
        System.out.print("\n");

        switch (cmdID) {
            case Settings.NET_TCP_CMD_EQUIP_ITEM:
                int netID = Bits.getInt(message, 0);
                int slot = Bits.getInt(message, 4);
                Item item = (Item) Server.game.getItemMap().get(netID);
                sender.getEquippedItems()[slot] = item;
                System.out.println("Der Client will Item equippen!" + item.stats.itemStats.get("name"));
                break;
        }
    }
}
