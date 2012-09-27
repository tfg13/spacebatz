package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_CHANGE_COLLISION extends FixedSizeSTCCommand {

    public STC_CHANGE_COLLISION() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Geänderten Boden übernehmen:
        int tx = Bits.getInt(data, 0);
        int ty = Bits.getInt(data, 4);
        int newCollision = Bits.getInt(data, 8);
        if (newCollision == 1) {
            GameClient.currentLevel.getCollisionMap()[tx][ty] = true;
        } else {
            GameClient.currentLevel.getCollisionMap()[tx][ty] = false;
        }
    }

    /**
     * Sendet eine Veränderung am Boden
     *
     * @param x X-Koordinate des geännderten Felds
     * @param y Y-Koordinate des geännderten Felds
     * @param newCollision neuer Kollisionswert (true - kollision; false - keine kollision)
     */
    public static void broadcastCollisionChange(int x, int y, boolean newCollision) {
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
}
