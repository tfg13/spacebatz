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
     * Sendet das Level an einen Client
     *
     * @param client der Client, an den gesendet wird
     */
    public void sendLevel(Client client) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_LEVEL, Server.game.getSerializedLevel(), client);
    }

    /**
     * Sendet enemytypes an einen Client
     *
     * @param client der Client, an den die enemytypes gesendet werden sollen
     */
    public void sendEnemyTypes(Client client) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_ENEMYTYPES, Server.game.getSerializedEnemyTypes(), client);
    }

    /**
     * Schickt dem Client einen neuen Player.
     *
     * @param client der Ziel-Client
     * @param player der neue Player
     */
    public void sendSetPlayer(Client client, Player player) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, player.netID);
        Bits.putFloat(b, 4, (float) player.getX());
        Bits.putFloat(b, 8, (float) player.getY());
        Server.serverNetwork.sendTcpData((byte) 21, b, client);
    }

    /**
     * Lässt den Client das Spiel starten.
     *
     * @param client der Ziel-Client
     */
    public void sendStartGame(Client client) {
        Server.serverNetwork.sendTcpData((byte) 22, new byte[1], client);
    }

    /**
     * Setzt die ClientID eines Clients auf dem Client.
     *
     * @param client der Client, dessen ID an ihn gesendet wird.
     */
    public void sendSetClientID(Client client) {
        Server.serverNetwork.sendTcpData((byte) 23, new byte[]{(byte) client.clientID}, client);
    }

    /**
     * Bullet/Mob-Angriff trifft Char
     *
     * @param netIDVictim netID des getroffenen Char
     * @param netIDAttacker netID des Bullets / Enemy
     * @param killed Ob Char getötet wird
     */
    public void sendCharHit(int netIDVictim, int damage, boolean killed) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[9];
            Bits.putInt(b, 0, netIDVictim);
            Bits.putInt(b, 4, damage);
            if (killed) {
                b[8] = 1;
            } else {
                b[8] = 0;
            }
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHAR_HIT, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_CHAR_HIT, b), c);
        }
    }

    /**
     * Item wird gedroppt
     */
    public void sendItemDrop(byte[] seritem) {
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_SPAWN_ITEM, seritem, c);
        }
    }

    /**
     * Item wird von Spieler aufgesammelt und kriegt eigenen Itemslot
     */
    public void sendItemGrab(int itemnetID, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[8];
            Bits.putInt(b, 0, itemnetID);
            Bits.putInt(b, 4, clientID);

            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_GRAB_ITEM, b, c);
        }
    }

    /**
     * Item wird von Spieler aufgesammelt und auf ein anderes draufgestackt
     */
    public void sendItemGrabToStack(int newitemnetID, int clientID, int stackitemID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[12];
            Bits.putInt(b, 0, newitemnetID);
            Bits.putInt(b, 4, clientID);
            Bits.putInt(b, 8, stackitemID);

            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_GRAB_ITEM_TO_STACK, b, c);
        }
    }

    /**
     * Item wird von Client angelegt
     */
    public void sendItemEquip(int itemnetID, byte selslot, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[9];
            Bits.putInt(b, 0, itemnetID);
            b[4] = selslot;
            Bits.putInt(b, 5, clientID);
            //Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_EQUIP_ITEM, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(Settings.NET_TCP_CMD_EQUIP_ITEM, b), c);
        }
    }

    /**
     * Item wird von Client abgelegt (zurück ins Inventar)
     */
    public void sendItemDequip(int slottype, byte selslot, byte droptoground, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[10];
            Bits.putInt(b, 0, slottype);
            b[4] = selslot;
            b[5] = droptoground;
            Bits.putInt(b, 6, clientID);
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_DEQUIP_ITEM, b, c);
        }
    }

    public void sendAllItems(Client client, HashMap<Integer, Item> items) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_ITEMS, Server.game.getSerializedItems(), client);
    }

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
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHANGE_GROUND, b, c);
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
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_CHANGE_COLLISION, b, c);
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
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_SWITCH_WEAPON, b, c);
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
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_ANSWER_RCON, b, sender);
    }
}
