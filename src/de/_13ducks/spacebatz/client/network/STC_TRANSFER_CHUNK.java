package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.util.Bits;

/**
 * Transferiert einen Chunk des Levels zum Client
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_TRANSFER_CHUNK extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int[][] ground = GameClient.currentLevel.ground;
        int[][] top = GameClient.currentLevel.top;
        byte[][] ground_random = GameClient.currentLevel.ground_randomize;
        byte[][] top_random = GameClient.currentLevel.top_randomize;
        boolean[][] col = GameClient.currentLevel.getCollisionMap();
        byte[][] shadow = GameClient.currentLevel.shadow;
        int startX = Bits.getInt(data, 0) * 8;
        int startY = Bits.getInt(data, 4) * 8;
        int dataIndex = 8;
        // Ground
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ground[startX + x][startY + y] = Bits.getInt(data, dataIndex);
                dataIndex += 4;
            }
        }
        // Top
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                top[startX + x][startY + y] = Bits.getInt(data, dataIndex);
                dataIndex += 4;
            }
        }
        // Ground_random
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ground_random[startX + x][startY + y] = data[dataIndex++];
            }
        }
        // Top_random
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                top_random[startX + x][startY + y] = data[dataIndex++];
            }
        }
        // Col
        for (int x = 0; x < 8; x++) {
            byte row = data[dataIndex++];
            for (int y = 0; y < 8; y++) {
                col[x + startX][y + startY] = (row & 1 << y) != 0;
            }
        }
        // Shadow
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                shadow[startX + x][startY + y] = data[dataIndex++];
            }
        }
        if (GameClient.getEngine() != null) {
            GameClient.getEngine().getGraphics().chunkReceived(Bits.getInt(data, 0), Bits.getInt(data, 4));
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        // Zwei mal Koordinaten, 2 * 64 ints texturen, 2 * texturrandoms, 8 byte kollisionsdaten
        return 8 * 8 * 4 * 2 + 2 * 8 * 8 + 8 + 8;
    }
}
