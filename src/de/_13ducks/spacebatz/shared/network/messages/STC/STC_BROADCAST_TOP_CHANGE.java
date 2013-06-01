package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_BROADCAST_TOP_CHANGE extends FixedSizeSTCCommand {

    public STC_BROADCAST_TOP_CHANGE() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Geänderte Wand übernehmen:
        int x = Bits.getInt(data, 0);
        int y = Bits.getInt(data, 4);
        int newTop = Bits.getInt(data, 8);
        GameClient.currentLevel.top[x][y] = newTop;
        GameClient.getEngine().getGraphics().minorTopChange(x, y);
    }

    /**
     * Sendet eine Veränderung an der Wand
     *
     * @param x X-Koordinate des geännderten Felds
     * @param y Y-Koordinate des geännderten Felds
     * @param newTop neuer wert der wand
     */
    public static void broadcastTopChange(int x, int y, int newTop) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, x);
        Bits.putInt(b, 4, y);
        Bits.putInt(b, 8, newTop);
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_CHANGE_TOP, b), c);
        }
    }
}
