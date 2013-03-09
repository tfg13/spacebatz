package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Player stibt / lebt wieder
 *
 * @author jk
 */
public class STC_PLAYER_TOGGLE_ALIVE extends FixedSizeSTCCommand {

    public STC_PLAYER_TOGGLE_ALIVE() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        int playerNetId = Bits.getInt(data, 0);
        boolean dead = false;
        if (data[4] == 1) {
            dead = true;
        }
        
        if (playerNetId == GameClient.player.netID) {
            // Hier Spieler beschimpfen 
        }
        
        PlayerCharacter p = (PlayerCharacter) GameClient.netIDMap.get(playerNetId);
        p.setDead(dead);
        if (dead) {
            p.setHealthpoints(0);
        } else {
            p.setHealthpoints(DefaultSettings.CHARHEALTH);
        }
        
    }
    
    public static void sendPlayerToggleAlive(int playerNetId, boolean dead) {
        for (Client c : Server.game.clients.values()) {
            byte[] b = new byte[5];
            Bits.putInt(b, 0, playerNetId);
            b[4] = 0;
            if (dead) {
               b[4] = 1; 
            }

            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_PLAYER_TOGGLE_ALIVE, b), c);
        }
    }
}
