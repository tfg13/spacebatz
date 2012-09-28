package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SET_PLAYER extends FixedSizeSTCCommand {

    public STC_SET_PLAYER() {
        super(12);
    }

    @Override
    public void execute(byte[] data) {
        // Player setzen
        GameClient.player = new PlayerCharacter(Bits.getInt(data, 0));
        GameClient.netIDMap.put(GameClient.player.netID, GameClient.player);
    }

    /**
     * Schickt dem Client einen neuen Player.
     *
     * @param client der Ziel-Client
     * @param player der neue Player
     */
    public static void sendSetPlayer(Client client, Player player) {
        byte[] b = new byte[12];
        Bits.putInt(b, 0, player.netID);
        Bits.putFloat(b, 4, (float) player.getX());
        Bits.putFloat(b, 8, (float) player.getY());
        //Server.serverNetwork.sendTcpData((byte) MessageIDs.NET_STC_SET_PLAYER, b, client);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_PLAYER, b), client);
    }
}
