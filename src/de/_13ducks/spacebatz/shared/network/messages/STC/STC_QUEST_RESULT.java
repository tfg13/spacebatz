package de._13ducks.spacebatz.shared.network.messages.STC;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.util.Bits;

/**
 * Wird gesendet, wenn ein Quest zuende ist.
 * Nachricht enthält Ergebnis, also
 * Erfolg, Misserfolg, Abgebrochen ohne Resultat.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class STC_QUEST_RESULT extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Einfach dem QuestManager die QuestID und den Grund geben.
        int questID = Bits.getInt(data, 0);
        byte result = data[4];
        GameClient.quests.questFinished(questID, result);
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 5;
    }

    /**
     * Sendet das Questergebnis zum Client.
     * @param questID die eindeutige ID des Quests
     * @param result das Ergebnis des Quests
     */
    public static void sendQuestResult(int questID, byte result) {
        byte[] data = new byte[5];
        Bits.putInt(data, 0, questID);
        data[4] = result;
        for (Client c : Server.game.clients.values()) {
            Server.serverNetwork2.queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_QUEST_RESULT, data), c);
        }
    }
}
