package de._13ducks.spacebatz.shared.network.messages.CTS;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.network.CTSCommand;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;

/**
 *
 * @author michael
 */
public class CTS_INVEST_SKILLPOINT extends CTSCommand {

    public CTS_INVEST_SKILLPOINT() {
        super();
    }

    @Override
    public void execute(Client client, byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        decoder.readByte();
        String ability = decoder.readString();
        client.getPlayer().investSkillpoint(ability);
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return sizeData;
    }

    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     */
    public static void sendInvestSkillPoint(String ability) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeByte((byte) 0); // Feld für Größe
        encoder.writeString(ability);
        byte data[] = encoder.getBytes();
        if (data.length < 128) {
            data[0] = (byte) data.length;
        }
        GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_INVEST_SKILLPOINT, data));
    }
}
