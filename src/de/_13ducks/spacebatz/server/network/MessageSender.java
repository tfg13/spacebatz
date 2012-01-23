package de._13ducks.spacebatz.server.network;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Player;
import de._13ducks.spacebatz.util.Bits;

/**
 * Sendet Daten übers Netzwerk.
 * Kümmert sich darum, gescheite Pakete zu backen.
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MessageSender {

    /**
     * Sendet das Level an einen Client
     */
    public void sendLevel(Client client) {
        Server.serverNetwork.sendTcpData((byte) 20, Server.game.getSerializedLevel(), client);
        System.out.println("Level sent, length was: " + Server.game.getSerializedLevel().length);
    }

    /**
     * Schickt dem Client einen neuen Player.
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
     * @param client der Ziel-Client
     */
    public void sendStartGame(Client client) {
        byte[] b = new byte[4];
        Bits.putInt(b, 0, Server.game.getTick());
        Server.serverNetwork.sendTcpData((byte) 22, b, client);
    }

    /**
     * Setzt die ClientID eines Clients auf dem Client.
     * @param client der Client, dessen ID an ihn gesendet wird.
     */
    public void sendSetClientID(Client client) {
        Server.serverNetwork.sendTcpData((byte) 23, new byte[]{(byte) client.clientID}, client);
    }

    /**
     * Benachrichtigt alle ANDEREN Clients, dass es einen neuen Player gibt.
     * @param client der Client, dem das NICHT geschickt wird.
     */
    public void sendNewPlayer(Client client) {
        for (Client c : Server.game.clients.values()) {
            if (c.equals(client)) {
                continue; // Der weiß das schon...
            }
            byte[] b = new byte[4];
            Bits.putInt(b, 0, client.getPlayer().netID);
            Server.serverNetwork.sendTcpData((byte) 24, b, c);
        }
    }

    public void sendAllChars(Client client) {
        for (Char c : Server.game.chars) {
            if (c.equals(client.getPlayer())) {
                continue; // Den eigenen nicht.
            }
            byte[] b = new byte[12];
            if (c instanceof Player) {
                Bits.putInt(b, 0, c.netID);
                Bits.putFloat(b, 4, (float) c.getX());
                Bits.putFloat(b, 8, (float) c.getY());
                Server.serverNetwork.sendTcpData((byte) 25, b, client);
            } else {
                Bits.putInt(b, 0, c.netID);
                Bits.putFloat(b, 4, (float) c.getX());
                Bits.putFloat(b, 8, (float) c.getY());
                Server.serverNetwork.sendTcpData((byte) 26, b, client);
            }
        }
    }

    /**
     * Sendet die Server-Tickrate an den Client.
     * @param client der Client
     */
    public void sendTickrate(Client client) {
        byte[] b = new byte[4];
        Bits.putInt(b, 0, Settings.SERVER_TICKRATE);
        Server.serverNetwork.sendTcpData((byte) 27, b, client);
    }
}
