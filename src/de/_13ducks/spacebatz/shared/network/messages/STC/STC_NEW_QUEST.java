package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.data.quest.ClientQuest;
import de._13ducks.spacebatz.client.data.quest.impl.BossQuest;
import de._13ducks.spacebatz.client.data.quest.impl.GotoQuest;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.shared.network.BitDecoder;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Wird gesendet, wenn ein neuer Quest aktiv wird.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_NEW_QUEST extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // quest suchen
        int questType = data[1];
        ClientQuest quest = null;
        switch (questType) {
            case 0:
                quest = new GotoQuest(Bits.getInt(data, 2), new float[]{Bits.getFloat(data, 6), Bits.getFloat(data, 10)});
                break;
            case 1:
                BitDecoder decoder = new BitDecoder(data);
                decoder.readByte(); // mysteriöses byte am anfang überspringen.... ist das die cmdid?
                decoder.readByte(); // Questtyp überspringen
                int questId = decoder.readInt();
                int targetNetId = decoder.readInt();
                float targetX = decoder.readFloat();
                float targetY = decoder.readFloat();
                quest = new BossQuest(questId, targetNetId, targetX, targetY);
                break;
            default:
                System.out.println("WARN: CNET: Received unknown questID, ignoring quest");
        }
        if (quest != null) {
            GameClient.quests.addQuest(quest);
        }
    }

    @Override
    public boolean isVariableSize() {
        return true;
    }

    @Override
    public int getSize(byte sizeData) {
        return sizeData;
    }

    public static void sendQuest(Quest quest) {
        byte[] data = quest.getClientData();
        byte[] realData = new byte[data.length + 1];
        realData[0] = (byte) (data.length + 1);
        System.arraycopy(data, 0, realData, 1, data.length);
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_NEW_QUEST, realData), c);
        }
    }
}
