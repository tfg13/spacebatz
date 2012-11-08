package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Bullet;
import de._13ducks.spacebatz.client.Enemy;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.util.Bits;

/**
 * Der Server lässt den Client eine neue Entity einfügen
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_ENTITY_CREATE extends STCCommand {

    @Override
    public void execute(byte[] data) {
        int netID = Bits.getInt(data, 2);
        if (GameClient.netIDMap.containsKey(netID)) {
            System.out.println("WARN: CNET: SYNC: Cannot insert Entity " + netID + " : id exists.");
            return;
        }
        byte type = data[1];
        switch (type) {
            case 2:
                PlayerCharacter pl = new PlayerCharacter(netID);
                GameClient.netIDMap.put(pl.netID, pl);
                break;
            case 3:
                Enemy en = new Enemy(netID, Bits.getInt(data, 6));
                GameClient.netIDMap.put(en.netID, en);
                break;
            case 4:
                Bullet bu = new Bullet(netID, Bits.getInt(data, 6), Bits.getInt(data, 10));
                GameClient.netIDMap.put(bu.netID, bu);
                break;
            default:
                System.out.println("WARN: CNET: SYNC: Unknown charTypeID (was " + type + ")");
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return sizeData;
    }
}
