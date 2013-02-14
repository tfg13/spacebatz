package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Versendet die aktuelle Turret-Drehung eines Spielers.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_PLAYER_TURRET_DIR_UPDATE extends FixedSizeSTCCommand {
    
    public STC_PLAYER_TURRET_DIR_UPDATE() {
        super(8);
    }

    @Override
    public void execute(byte[] data) {
        int clientID = Bits.getInt(data, 0);
        float rot = Bits.getFloat(data, 4);
        Char player = GameClient.netIDMap.get(clientID);
        if (player != null) {
            ((PlayerCharacter) player).setTurretDir(rot);
        }
    }
    
    public static void broadcastTurretDir(int netID, float dir) {
        byte[] data = new byte[8];
        Bits.putInt(data, 0, netID);
        Bits.putFloat(data, 4, dir);
        for (Client c: Server.game.clients.values()) {
            if (c.getPlayer().netID == netID) {
                continue; // Der weiß seine Drehung selber
            }
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_UPDATE_TURRET_DIR, data), c);
        }
    }
}
