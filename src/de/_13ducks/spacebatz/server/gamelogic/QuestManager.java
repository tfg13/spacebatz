package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_NEW_QUEST;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_QUEST_RESULT;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Verwaltet die Quests.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class QuestManager {

    /**
     * Alle aktiven Quests.
     */
    private ArrayList<Quest> activeQuests = new ArrayList<>();
    /**
     * Quests, die im nächsten Tick hinzugefügt werden.
     * Notwendig, sonst funktioniert der Iterator in tick() nicht sauber, wenn neue Quests von alten Quests eingefügt werden.
     */
    private ArrayList<Quest> newQuests = new ArrayList<>();

    /**
     * Muss ein Mal pro Tick aufgerufen werden, tickt alle aktiven Quests.
     */
    public void tick() {
        // Neue Quests hinzufügen
        activeQuests.addAll(newQuests);
        for (Quest q : newQuests) {
            // An Client senden
            if (!q.isHidden()) {
                STC_NEW_QUEST.sendQuest(q);
            }
        }
        newQuests.clear();
        Iterator<Quest> iter = activeQuests.iterator();
        while (iter.hasNext()) {
            Quest quest = iter.next();
            try {
                // Quest ticken, dann Status abfragen
                quest.tick();
                int state = quest.check();
                // Bei -1 wurde das Statusberechnen verschoben, das wird nur ein Mal pro Tick gemacht
                if (state != -1) {
                    if (state == Quest.STATE_RUNNING) {
                        // Nichts spannendes, weiterlaufen lassen
                        continue;
                    }
                    if (state == Quest.STATE_COMPLETED) {
                        // Erfolgreich
                        //@TODO: Belohnung verteilen?
                        System.out.println("[INFO][QuestManager]: Quest " + quest.getName() + " completed.");
                    } else if (state == Quest.STATE_FAILED) {
                        System.out.println("[INFO][QuestManager]: Quest " + quest.getName() + " failed.");
                    } else if (state == Quest.STATE_ABORTED) {
                        System.out.println("[WARNING][QuestManager]: Quest " + quest.getName() + " was aborted for unknown reasons...");
                    }
                    // Jeder der drei Zustände bedeutet, dass der Quest in Zukunft nichtmehr laufen muss
                    iter.remove();
                    // Client quest entfernen lassen
                    STC_QUEST_RESULT.sendQuestResult(quest.questID, (byte) state);
                }
            } catch (Exception ex) {
                // Quest ist abgestürzt - Loggen und Quest löschen
                iter.remove();
                System.out.println("[ERROR][QuestManager]: Quest " + quest.getName() + " crashed and has been deleted. Exception:");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Fügt einen neuen Quest hinzu.
     * Es ist sicher, diese Methode während der Bearbeitung eines anderen Quests aufzurufen.
     *
     * @param quest der neue Quest, läuft ab dem nächsten Tick mit
     */
    public void addQuest(Quest quest) {
        newQuests.add(quest);
    }

    /**
     * Aufrufen, wenn ein neuer Client eingefügt wurde.
     * Sendet alle aktiven, nicht geheimen Quests an den neuen.
     *
     * @param client
     */
    void newClient(Client client) {
        for (Quest quest : activeQuests) {
            if (!quest.isHidden()) {
                STC_NEW_QUEST.sendQuest(quest);
            }
        }
    }
}
