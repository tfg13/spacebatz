package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Plant;
import de._13ducks.spacebatz.server.data.Player;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.Bits;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Sendet Daten übers Netzwerk. Kümmert sich darum, gescheite Pakete zu backen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ServerMessageSender {

    /**
     * Sendet das Level an einen Client
     */
    public void sendLevel(Client client) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_LEVEL, Server.game.getSerializedLevel(), client);
    }

    /**
     * Sendet enemytypes an einen Client
     */
    public void sendEnemyTypes(Client client) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_ENEMYTYPES, Server.game.getSerializedEnemyTypes(), client);
    }

    /**
     * Sendet bullettypes an einen Client
     */
    public void sendBulletTypes(Client client) {
        Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_TRANSFER_BULLETTYPES, Server.game.getSerializedBulletTypes(), client);
    }

    /**
     * Schickt dem Client einen neuen Player.
     *
     * @param client der Ziel-Client
     * @param player der neue Player
     */
    public void sendSetPlayer(Client client, Player player) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, player.getNetID());
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
        byte[] b = new byte[4];
        Bits.putInt(b, 0, Server.game.getTick());
        Server.serverNetwork.sendTcpData((byte) 22, b, client);
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
     * Bullet trifft Char
     *
     * @param netIDChar netID des getroffenen Char
     * @param netIDBullet netID des Bullets
     * @param killed Ob Bullet den Char tötet
     */
    public void sendHitChar(int netIDChar, int netIDBullet, boolean killed) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[9];
            Bits.putInt(b, 0, netIDChar);
            Bits.putInt(b, 4, netIDBullet);
            if (killed) {
                b[8] = 1;
            } else {
                b[8] = 0;
            }
            Server.serverNetwork.sendTcpData((byte) 28, b, c);
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
     * Item wird von Spieler aufgesammelt
     */
    public void sendItemGrab(int itemnetID, int clientID) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[8];
            Bits.putInt(b, 0, itemnetID);
            Bits.putInt(b, 4, clientID);
            Server.serverNetwork.sendTcpData(Settings.NET_TCP_CMD_GRAB_ITEM, b, c);
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
     * Sendet Alle Pflanzen an den Client
     */
    public void sendAllPlants() {
        for (Plant p : Server.game.getPlants()) {
            broadcastGroundChange(p.getX(), p.getY(), p.getTex());
        }
    }
}
