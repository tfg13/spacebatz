package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.util.Bits;

/**
 * Transferiert einen Chunk des Levels zum Client
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_TRANSFER_CHUNK extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int[][] ground = GameClient.currentLevel.getGround();
        boolean[][] col = GameClient.currentLevel.getCollisionMap();
        int startX = Bits.getInt(data, 0) * 8;
        int startY = Bits.getInt(data, 4) * 8;
        int dataIndex = 8;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ground[startX + x][startY + y] = Bits.getInt(data, dataIndex);
                dataIndex += 4;
            }
        }
        for (int x = 0; x < 8; x++) {
            byte row = data[dataIndex++];
            for (int y = 0; y < 8; y++) {
                col[x + startX][y + startY] = (row & 1 << y) != 0;
            }
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        // Zwei mal Koordinaten, 64 ints texturen, 8 byte kollisionsdaten
        return 64 * 4 + 8 + 8;
    }
    
}