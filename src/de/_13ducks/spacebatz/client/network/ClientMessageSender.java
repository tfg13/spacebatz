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

    

   

   public void sendDisconnect() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CLIENT_DISCONNECT, new byte[0]));

    }

    

   

    void sendRconRequest() {
        //Client.getNetwork().sendTcpData(Settings.NET_TCP_CMD_REQUEST_RCON, new byte[1]);
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_REQUEST_RCON, new byte[0]));

    }
}
