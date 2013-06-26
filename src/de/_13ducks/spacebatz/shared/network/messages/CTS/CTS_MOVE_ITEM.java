package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Fordert den Server auf, ein Item im Inventar/Rucksack in einen anderen Slot zu verschieben.
 *
 * @author mekhar
 */
public class CTS_MOVE_ITEM extends FixedSizeCTSCommand {

    public CTS_MOVE_ITEM() {
        super(8);
    }

    @Override
    public void execute(Client client, byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        int from = decoder.readInt();
        int to = decoder.readInt();
        client.getPlayer().tryMoveItem(from, to);
    }

    /**
     * Sendet das Kommando zum Bewegen von Items.
     *
     * @param from der Slot von dem das Item kommt
     * @param to der Slot in den es verschoben werden soll
     */
    public static void sendMoveItem(int from, int to) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeInt(from);
        encoder.writeInt(to);
        byte[] data = encoder.getBytes();
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_MOVE_ITEM, data));
    }
}
