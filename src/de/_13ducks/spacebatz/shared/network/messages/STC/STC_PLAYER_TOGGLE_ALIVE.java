package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
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
        int clientID = Bits.getInt(data, 0);
        boolean dead = false;
        if (data[4] == 1) {
            dead = true;
        }

        if (clientID == GameClient.logicPlayer.clientID) {
            // Hier Spieler beschimpfen 
        }

        GameClient.players.get(clientID).setDead(dead);
        PlayerCharacter p = GameClient.players.get(clientID).getPlayer();
        if (p != null) {
            if (dead) {
                p.setHealthpoints(0);
            } else {
                p.setHealthpoints(CompileTimeParameters.CHARHEALTH);
            }
        }
    }

    public static void sendPlayerToggleAlive(int clientID, boolean dead) {
        byte[] b = new byte[5];
        Bits.putInt(b, 0, clientID);
        b[4] = 0;
        if (dead) {
            b[4] = 1;
        }

        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_TCP_CMD_PLAYER_TOGGLE_ALIVE, b), c);
        }
    }
}
