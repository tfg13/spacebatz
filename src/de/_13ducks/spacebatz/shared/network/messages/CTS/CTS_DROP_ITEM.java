package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.FixedSizeCTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 * Fordert den Server auf, ein Item zu löschen.
 *
 * @author mekhar
 */
public class CTS_DROP_ITEM extends FixedSizeCTSCommand {

    public CTS_DROP_ITEM() {
        super(4);
    }

    @Override
    public void execute(Client client, byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        int slot = decoder.readInt();
        client.getPlayer().tryDropItem(slot);
    }

    /**
     * Sendet das Kommando zum ein Item zerstören.
     *
     * @param slot der Slot des Items das zerstört werden soll.
     */
    public static void sendDropItem(int slot) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeInt(slot);
        byte[] data = encoder.getBytes();
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_DROP_ITEM, data));
    }
}
