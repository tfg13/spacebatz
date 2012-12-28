package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Wird gesendet, wenn sich die Beleuchtung geändert hat.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_SHADOW_CHANGE extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int px = Bits.getInt(data, 0);
        int py = Bits.getInt(data, 4);
        int index = 8;
        byte[][] shadow = GameClient.currentLevel.shadow;
        for (int x = -15; x <= 15; x++) {
            for (int y = -15; y <= 15; y++) {
                if (px + x < 0 || py + y < 0 || px + x >= shadow.length || py + y >= shadow[0].length) {
                    index++;
                    continue;
                }
                shadow[px + x][py + y] = data[index++];
            }
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 31 * 31 + 8;
    }

    public static void sendShadowChange(int px, int py, byte[][] changeMap) {
        byte[] data = new byte[31 * 31 + 8];
        Bits.putInt(data, 0, px);
        Bits.putInt(data, 4, py);
        int index = 8;
        for (int x = 0; x < 31; x++) {
            System.arraycopy(changeMap[x], 0, data, index, 31);
            index += 31;
        }
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_SHADOW_CHANGE, data), c);
        }
    }
}
