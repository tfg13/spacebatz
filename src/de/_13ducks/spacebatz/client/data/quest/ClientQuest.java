package de._13ducks.spacebatz.client.data.quest;

import de._13ducks.spacebatz.client.graphics.Renderer;

/**
 * Client-Version eines Quests
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class ClientQuest {
    
    /**
     * Die QuestID, zur eindeutigen Zuordnung zwischen Server und Client.
     */
    public final int questID;
    
    /**
     * Erstellt einen neuen ClientQuest mit der gegebenen QuestID
     * @param questID 
     */
    public ClientQuest(int questID) {
        this.questID = questID;
    }
    
    /**
     * Liefert true, wenn der Quest Infos im Quest-Hud anzeigen kann.
     * Genau dann muss auch renderVisual() was sinnvolles machen.
     * @return true, wenn der Quest Infos im Quest-Hud anzeigen kann
     */
    public abstract boolean hasVisual();
    
    /**
     * Rendert das Quest-Hud für diesen Quest.
     * Undefiniert, wenn hasVisual() false liefert.
     * @param renderer der aktuelle Renderer für simples Zeichnen. Muss aber nicht benutzt werden
     */
    public abstract void renderVisual(Renderer renderer);
}
