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
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import java.util.HashMap;

/**
 * Sendet Daten übers Netzwerk. Kümmert sich darum, gescheite Pakete zu backen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ServerMessageSender {

   

    

  

   

   

    

    


   

    

  

    

    /**
     * Sendet die Server-Tickrate an den Client.
     *
     * @param client der Client
     */
    public void sendTickrate(Client client) {
        byte[] b = new byte[4];
        Bits.putInt(b, 0, Settings.SERVER_TICKRATE);
        Server.serverNetwork.sendTcpData((byte) 27, b, client);
    }

    /**
     * Sendet eine Veränderung am Boden
     *
     * @param x X-Koordinate des geännderten Felds
     * @param y Y-Koordinate des geännderten Felds
     * @param newGround neuer wert des bodens
     */
    public void broadcastGroundChange(int x, int y, int newGround) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, x);
        Bits.putInt(b, 4, y);
        Bits.putInt(b, 8, newGround);
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHANGE_GROUND, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CHANGE_GROUND, b), c);
        }
    }

    /**
     * Sendet eine Veränderung am Boden
     *
     * @param x X-Koordinate des geännderten Felds
     * @param y Y-Koordinate des geännderten Felds
     * @param newCollision neuer Kollisionswert (true - kollision; false - keine kollision)
     */
    public void broadcastCollisionChange(int x, int y, boolean newCollision) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, x);
        Bits.putInt(b, 4, y);
        if (newCollision) {
            Bits.putInt(b, 8, 1);
        } else {
            Bits.putInt(b, 8, 0);
        }
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHANGE_COLLISION, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CHANGE_COLLISION, b), c);
        }
    }

    /**
     * Client wählt andere Waffe aus
     */
    public void sendWeaponswitch(Client client, byte slot) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, client.clientID);
        b[4] = slot;
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_SWITCH_WEAPON, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_SWITCH_WEAPON, b), c);

        }
    }

    /**
     * Antwortet dem Client mit ja oder nein auf seine rcon-anfrage
     *
     * @param sender
     * @param answer
     * @param port
     */
    void sendRconAnswer(Client sender, boolean answer, int port) {
        byte[] b = new byte[5];
        b[0] = (byte) (answer ? 1 : 0);
        Bits.putInt(b, 1, port);
        //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_ANSWER_RCON, b, sender);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_ANSWER_RCON, b), sender);

    }
}
