package de._13ducks.spacebatz.shared.network.messages.STC;

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
public class STC_SET_SKILL_MAPPING extends STCCommand {

    public STC_SET_SKILL_MAPPING() {
    }

    @Override
    public void execute(byte[] data) {
        BitDecoder decoder = new BitDecoder(data);
        decoder.readByte();// Länge skippen
        String skill = decoder.readString();
        byte slot = decoder.readByte();
        GameClient.getEngine().getGraphics().getSkillTree().setSkillMapping(skill, slot);
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
     * Sendet ein Update des Skilltrees an einen Client.
     *
     * @param skill der Skill der aktualisiert wird
     * @param level das Level des Skills. -1 falls der Skill nicht verfügbar ist, 0 wenn er verfügbar ist aber noch nicht gelevelt wurde oder x wenn er level x hat.
     */
    public static void sendSetSkillMapping(Client client, String skill, byte slot) {
        BitEncoder encoder = new BitEncoder();
        encoder.writeByte((byte) 0); // Platzhalter, da kommt später die Größe rein
        encoder.writeString(skill);
        encoder.writeByte(slot);
        byte data[] = encoder.getBytes();
        data[0] = (byte) data.length;
        Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_STC_SET_SKILL_MAPPING, data), client);
    }
}
