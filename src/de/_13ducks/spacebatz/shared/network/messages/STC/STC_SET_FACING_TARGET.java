package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class STC_SET_FACING_TARGET extends FixedSizeSTCCommand {

    public STC_SET_FACING_TARGET() {
        super(9);
    }

    @Override
    public void execute(byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        boolean isFacing = decoder.readBoolean();
        int facerNetId = decoder.readInt();
        int facingTargetNetId = decoder.readInt();
        Char enemy = GameClient.netIDMap.get(facerNetId);
        if (enemy != null) {
            if (isFacing) {
                enemy.setFacing(facingTargetNetId);
            } else {
                enemy.stopFacing();
            }
        } else {
            System.out.println("CLIENT: Received setFacing command for unknown entity!");
        }

    }

    public static void sendSetFacingTarget(boolean isFacing, int facerNetId, int targetNetId) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeBoolean(isFacing);
        encoder.writeInt(facerNetId);
        encoder.writeInt(targetNetId);
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.STC_SET_FACING_TARGET, encoder.getBytes()), c);
        }
    }
}
