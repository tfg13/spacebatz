package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;

/**
 * Neue Bewegungsdaten für die Interpolierten Gegner
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_ENTITY_DISCRETE_UPDATE extends STCCommand {

    @Override
    public void execute(byte[] data) {
        for (int i = 0; i < (data.length - 1) / 12; i++) {
            // TEST: Erstmal Einheit nur an die vorhergesagte Position setzen:
            int netID = Bits.getInt(data, i * 12 + 1);
            Char c = GameClient.netIDMap.get(netID);
            if (c == null) {
                System.out.println("WARNING: CNET: MOVESYNCD: Skipping unknown Char " + netID);
                continue;
            }
            Movement m = new Movement(Bits.getFloat(data, i * 12 + 5), Bits.getFloat(data, i * 12 + 9), 0, 0, -1, 1);
            c.applyMove(m);
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        int number = sizeData;
        if (number < 0) {
            number += 256;
        }
        return number * 12 + 1;
    }
}
