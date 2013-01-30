package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_BROADCAST_GROUND_CHANGE extends FixedSizeSTCCommand {

    public STC_BROADCAST_GROUND_CHANGE() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Geänderten Boden übernehmen:
        int x = Bits.getInt(data, 0);
        int y = Bits.getInt(data, 4);
        int newGround = Bits.getInt(data, 8);
        GameClient.currentLevel.ground[x][y] = newGround;
    }

    /**
     * Sendet eine Veränderung am Boden
     *
     * @param x X-Koordinate des geännderten Felds
     * @param y Y-Koordinate des geännderten Felds
     * @param newGround neuer wert des bodens
     */
    public static void broadcastGroundChange(int x, int y, int newGround) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, x);
        Bits.putInt(b, 4, y);
        Bits.putInt(b, 8, newGround);
        for (Client c : Server.game.clients.values()) {
            //Server.serverNetwork.sendTcpData(MessageIDs.NET_TCP_CMD_CHANGE_GROUND, b, c);
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_CHANGE_GROUND, b), c);
        }
    }
}
