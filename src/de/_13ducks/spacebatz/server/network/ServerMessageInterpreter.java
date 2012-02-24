package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
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
                byte selectedslot = message[4];
                Item item = sender.getInventory().getItems().get(netID);

                // richtiger Itemtyp für diesen Slot?
                int slottype = (int) item.getStats().itemStats.get("itemclass");

                if (sender.getEquippedItems().getEquipslots()[slottype] != null) {
                    if (item != null) {
                        if (sender.getEquippedItems().getEquipslots()[slottype][selectedslot] != null) {
                        // da ist schon ein Item -> ins Inventar
                        Item moveitem = sender.getEquippedItems().getEquipslots()[slottype][selectedslot];
                        sender.getInventory().getItems().put(moveitem.getNetID(), moveitem);
                        Server.msgSender.sendItemDequip(slottype, selectedslot, sender.clientID);
                        }
                        // Jetzt neues Item anlegen
                        sender.getEquippedItems().getEquipslots()[slottype][selectedslot] = item;
                        sender.getInventory().getItems().remove(item.getNetID());
                        sender.getPlayer().calcEquipStats();
                        // Item-Anleg-Befehl zum Client senden
                        Server.msgSender.sendItemEquip(item.getNetID(), selectedslot, sender.clientID);

                    }
                }
                break;
            case Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP:
                int slottype2 = Bits.getInt(message, 0);
                byte selslot = message[4];
                if (sender.getEquippedItems().getEquipslots()[slottype2] != null) {
                    if (sender.getEquippedItems().getEquipslots()[slottype2][selslot] != null) {
                        Item itemx = sender.getEquippedItems().getEquipslots()[slottype2][selslot];
                        sender.getInventory().getItems().put(itemx.getNetID(), itemx);
                        sender.getEquippedItems().getEquipslots()[slottype2][selslot] = null;
                        sender.getPlayer().calcEquipStats();
                        Server.msgSender.sendItemDequip(slottype2, selslot, sender.clientID);
                    }
                }

                break;
            case Settings.NET_TCP_CMD_CLIENT_DISCONNECT:
                Server.disconnectClient(sender);
                break;
            default:
                System.out.println("WARNING: Received CTS-TCP with unknown cmdid! (was " + cmdID + ")");
        }
    }
}
