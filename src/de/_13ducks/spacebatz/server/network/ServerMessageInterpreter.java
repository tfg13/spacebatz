package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.data.Client;
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

        switch (cmdID) {
            case Settings.NET_TCP_CMD_REQUEST_ITEM_EQUIP:
                int netID = Bits.getInt(message, 0);
                int slot = Bits.getInt(message, 4);
                Item item = sender.getInventory().getItems().get(netID);

                // richtiger Itemtyp für diesen Slot?
                if (item.getStats().itemStats.get("itemclass") == slot) {
                    if (sender.getEquippedItems()[slot] != null) {
                        // da ist schon ein Item -> ins Inventar
                        Item moveitem = sender.getEquippedItems()[slot];
                        sender.getInventory().getItems().put(moveitem.getNetID(), moveitem);
                        Server.msgSender.sendItemDequip(slot, sender.clientID);
                    }
                    sender.getEquippedItems()[slot] = item;
                    // Jetzt neues Item anlegen
                    sender.getEquippedItems()[slot] = item;
                    sender.getInventory().getItems().remove(item.getNetID());
                    // Item-Anleg-Befehl zum Client senden
                    Server.msgSender.sendItemEquip(item.getNetID(), sender.clientID);
                }
                break;
            case Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP:
                int slot2 = Bits.getInt(message, 0);
                if (sender.getEquippedItems()[slot2] != null) {
                    sender.getInventory().getItems().put(sender.getEquippedItems()[slot2].getNetID(), sender.getEquippedItems()[slot2]);
                    sender.getEquippedItems()[slot2] = null;
                    Server.msgSender.sendItemDequip(slot2, sender.clientID);
                }

                break;
        }
    }
}
