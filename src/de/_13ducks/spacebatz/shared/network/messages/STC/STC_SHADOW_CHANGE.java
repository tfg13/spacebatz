package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.mapgen.data.Vector;

/**
 * Wird gesendet, wenn sich ein Chunk der Beleuchtung geändert hat.
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
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                shadow[px * 8 + x][py * 8 + y] = data[index++];
            }
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 8 * 8 + 8;
    }

    public static void sendShadowChange(Vector chunk) {
        byte[] data = new byte[8 * 8 + 8];
        Bits.putInt(data, 0, (int) chunk.x);
        Bits.putInt(data, 4, (int) chunk.y);
        int index = 8;
        byte[][] shadow = Server.game.getLevel().shadow;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                data[index++] = shadow[(int) chunk.x * 8 + x][(int) chunk.y * 8 + y];
            }
        }
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_SHADOW_CHANGE, data), c);
        }
    }
}
