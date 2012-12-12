package de._13ducks.spacebatz.client.logic;

import de._13ducks.spacebatz.client.data.quest.ClientQuest;
import java.util.HashMap;

/**
 * Verwaltet die Quests auf dem Client
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientQuestManager {
    
    /**
     * Der aktuelle Primärquest.
     */
    private ClientQuest current = null;
    
    /**
     * Alle aktiven Quests.
     */
    private HashMap<Integer, ClientQuest> activeQuests = new HashMap<>();
    
    public int getNumberOfActiveQuests() {
        return activeQuests.size();
    }
    
    /**
     * Liefert den aktiven PrimärQuest.
     * Null, wenn kein Quest aktiv ist.
     * @return der Hauptquest oder null
     */
    public ClientQuest getCurrentQuest() {
        return current;
    }

    /**
     * Fügt einen Quest hinzu.
     * Wenn es noch keinen current gibt, wird dieser current.
     * Sonst bleibt der alte current.
     * @param quest der neue Quest
     */
    public void addQuest(ClientQuest quest) {
        activeQuests.put(quest.questID, quest);
        if (current == null) {
            current = quest;
        }
    }

    public void questFinished(int questID, byte result) {
        ClientQuest quest = activeQuests.get(questID);
        if (quest != null) {
            // TODO: Compute result
            activeQuests.remove(questID);
        } else {
            System.out.println("WARN: CQUEST: Cannot compute quest result, id " + questID + " unknown");
        }
    }

}
