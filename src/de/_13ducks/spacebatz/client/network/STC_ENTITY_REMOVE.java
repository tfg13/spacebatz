package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_ENTITY_REMOVE extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int netID = Bits.getInt(data, 0);
        if (GameClient.netIDMap.containsKey(netID)) {
            // Löschen
            GameClient.netIDMap.remove(netID);
        } else {
            System.out.println("WARN: CNET: SYNC: Cannot remove entity " + netID + " : no such entity");
        }
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 4;
    }
}
