package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.PlayerCharacter;
import de._13ducks.spacebatz.client.data.LogicPlayer;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 *
 * @author michael
 */
public class STC_SET_PLAYER extends FixedSizeSTCCommand {

    public STC_SET_PLAYER() {
        super(8);
    }

    @Override
    public void execute(byte[] data) {
        // Player setzenBits.getInt(data, 0)
        int netID = Bits.getInt(data, 0);
        float size = Bits.getFloat(data, 4);
        GameClient.player = new PlayerCharacter(netID, size);
        GameClient.player.setPrediction(DefaultSettings.CLIENT_ENABLE_PREDICTION);
        GameClient.logicPlayer.setPlayerNetID(netID);
        GameClient.logicPlayer.setPlayer(GameClient.player);
        GameClient.netIDMap.put(GameClient.player.netID, GameClient.player);
    }

    /**
     * Schickt dem Client einen neuen Player.
     *
     * @param client der Ziel-Client
     * @param player der neue Player
     */
    public static void sendSetPlayer(Client client, Player player) {
        byte[] b = new byte[8];
        Bits.putInt(b, 0, player.netID);
        Bits.putFloat(b, 4, (float) player.getSize());
        //Server.serverNetwork.sendTcpData((byte) MessageIDs.NET_STC_SET_PLAYER, b, client);
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_PLAYER, b), client);
    }
}
