package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.Char;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.FixedSizeSTCCommand;
import de._13ducks.spacebatz.client.network.STCCommand;
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
public class STC_SET_LOOK_IN_MOVING_DIRECTION extends FixedSizeSTCCommand {

    public STC_SET_LOOK_IN_MOVING_DIRECTION() {
        super(5);
    }

    @Override
    public void execute(byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        int netId = decoder.readInt();
        boolean lookInMovingDirection = decoder.readBoolean();
        Char target = GameClient.netIDMap.get(netId);
        if (target != null) {
            target.lookInMovingDirection = lookInMovingDirection;
        } else {
            System.out.println();
        }
    }

    /**
     * Sendet ein Update des Skilltrees an einen Client.
     *
     * @param skill der Skill der aktualisiert wird
     * @param level das Level des Skills. -1 falls der Skill nicht verfügbar
     * ist, 0 wenn er verfügbar ist aber noch nicht gelevelt wurde oder x wenn
     * er level x hat.
     */
    public static void sendSetLookInMovingDirection(int netId, boolean lookInMovingDirection) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeInt(netId);
        encoder.writeBoolean(lookInMovingDirection);
        byte data[] = encoder.getBytes();
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_LOOK_IN_MOVING_DIRECTION, data), c);
        }
    }
}
