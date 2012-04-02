/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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
            interpretTCPMessage(m.getCmdID(), m.getData(), m.getClient());
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
//                int netID = Bits.getInt(message, 0);
//                byte selectedslot = message[4];
//                
//                Item item = sender.getPlayer().getInventory().getItems().get(netID);
//
//                // richtiger Itemtyp für diesen Slot?
//                int slottype = (int) item.getStats().itemStats.get("itemclass");
//
//                if (sender.getPlayer().getEquippedItems().getEquipslots()[slottype] != null) {
//                    if (item != null) {
//                        if (sender.getPlayer().getEquippedItems().getEquipslots()[slottype][selectedslot] != null) {
//                            // da ist bereits ein Item
//                            Item moveitem = sender.getPlayer().getEquippedItems().getEquipslots()[slottype][selectedslot];
//
//                            sender.getPlayer().getInventory().getItems().put(moveitem.getNetID(), moveitem);
//                            Server.msgSender.sendItemDequip(slottype, selectedslot, (byte) 0, sender.clientID);
//                        }
//                        // Jetzt neues Item anlegen
//                        sender.getPlayer().getEquippedItems().getEquipslots()[slottype][selectedslot] = item;
//                        sender.getPlayer().getInventory().getItems().remove(item.getNetID());
//                        sender.getPlayer().calcEquipStats();
//                        // Item-Anleg-Befehl zum Client senden
//                        Server.msgSender.sendItemEquip(item.getNetID(), selectedslot, sender.clientID);
//
//                    }
//                }
                break;
            case Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP:
//                int slottype2 = Bits.getInt(message, 0);
//                byte selslot = message[4];
//                if (sender.getPlayer().getEquippedItems().getEquipslots()[slottype2] != null) {
//                    if (sender.getPlayer().getEquippedItems().getEquipslots()[slottype2][selslot] != null) {
//                        Item itemx = sender.getPlayer().getEquippedItems().getEquipslots()[slottype2][selslot];
//                        sender.getPlayer().getEquippedItems().getEquipslots()[slottype2][selslot] = null;
//                        sender.getPlayer().calcEquipStats();
//                        // passt das Item ins Inventar?
//                        if (sender.getPlayer().getInventory().getItems().size() < Settings.INVENTORY_SIZE) {
//                            sender.getPlayer().getInventory().getItems().put(itemx.getNetID(), itemx);
//                            Server.msgSender.sendItemDequip(slottype2, selslot, (byte) 0, sender.clientID);
//                        } else {
//                            Server.msgSender.sendItemDequip(slottype2, selslot, (byte) 1, sender.clientID);
//
//                            itemx.setPosX(sender.getPlayer().getX());
//                            itemx.setPosY(sender.getPlayer().getY());
//                            Server.game.getItemMap().put(itemx.getNetID(), itemx);
//                            byte[] serializedItem = null;
//                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
//                            ObjectOutputStream os;
//                            try {
//                                os = new ObjectOutputStream(bs);
//                                os.writeObject(itemx);
//                                os.flush();
//                                bs.flush();
//                                bs.close();
//                                os.close();
//                                serializedItem = bs.toByteArray();
//                            } catch (IOException ex) {
//                                ex.printStackTrace();
//                            }
//                            Server.msgSender.sendItemDrop(serializedItem);
//                        }
//                    }
//                }
                break;
            case Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH:
//                byte selslot2 = message[0];
//                sender.getPlayer().selectAttack(selslot2);
//                Server.msgSender.sendWeaponswitch(sender, selslot2);
                break;
            case Settings.NET_TCP_CMD_CLIENT_DISCONNECT:
                Server.disconnectClient(sender);
                break;
            case Settings.NET_TCP_CMD_REQUEST_RESYNC:
                Server.serverNetwork.udp.resyncClient(sender);
                break;
            case Settings.NET_TCP_CMD_REQUEST_RCON:
                Server.msgSender.sendRconAnswer(sender, Settings.SERVER_ENABLE_RCON, Settings.SERVER_RCONPORT);
                break;
            default:
                System.out.println("WARNING: Received CTS-TCP with unknown cmdid! (was " + cmdID + ")");
        }
    }
}
