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
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Die Sendekpomponente des Netzwerkmoduls
 *
 * @author michael
 */
public class ClientMessageSender {

    

    /**
     * Client will Item ablegen, muss dafür aber erst Server fragen
     */
    public void sendDequipItem(int slottype, byte selslot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, slottype);
        b[4] = selslot;
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, b);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, b));

    }

    /**
     * Client will andere Waffe auswählen
     */
    public void sendSwitchWeapon(byte slot) {
        byte[] b = new byte[1];
        b[0] = slot;
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH, b);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_WEAPONSWITCH, b));

    }

    public void sendDisconnect() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[0]));

    }

    void sendRequestResync() {
        // Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RESYNC, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_RESYNC, new byte[0]));

    }

    void sendRconRequest() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RCON, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_RCON, new byte[0]));

    }
}
