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
import de._13ducks.spacebatz.util.Bits;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Interpretiert von dem Clients empfangene Nachrichten
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
//                sender.getPlayer().clientEquipItem(netID, selectedslot);
                break;
            case Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP:
//                int slottype = Bits.getInt(message, 0);
//                byte selslot = message[4];
//                sender.getPlayer().clientDequipItem(slottype, selslot);
                break;
            case Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH:
//                byte selslot2 = message[0];
//                sender.getPlayer().clientSelectWeapon(selslot2);
                break;
            case Settings.NET_TCP_CMD_CLIENT_DISCONNECT:
//                Server.disconnectClient(sender);
                break;
            case Settings.NET_TCP_CMD_REQUEST_RESYNC:
//                Server.serverNetwork.udp.resyncClient(sender);
                break;
            case Settings.NET_TCP_CMD_REQUEST_RCON:
//                Server.msgSender.sendRconAnswer(sender, Settings.SERVER_ENABLE_RCON, Settings.SERVER_RCONPORT);
                break;
            default:
                System.out.println("WARNING: Received CTS-TCP with unknown cmdid! (was " + cmdID + ")");
        }
    }
}
