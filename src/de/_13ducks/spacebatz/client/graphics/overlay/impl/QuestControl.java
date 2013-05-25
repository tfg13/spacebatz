package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.renderer.impl.GameRenderer;
import static org.lwjgl.opengl.GL11.*;

/**
 * Zeigt Infos über Quests.
 * Anzahl verfügbarer/aktiver Quests, aktuellen Primärquest
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class QuestControl extends Overlay {

    @Override
    public void render() {
        glDisable(GL_TEXTURE_2D);
        glColor4f(.9f, .9f, .9f, .7f);
        // Quest-Infos anzeigen?
        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
            glRectf(GameRenderer.tilesX - 8, GameRenderer.tilesY, GameRenderer.tilesX, GameRenderer.tilesY - 2.5f);
        } else {
            glRectf(GameRenderer.tilesX - 8, GameRenderer.tilesY, GameRenderer.tilesX, GameRenderer.tilesY - 0.5f);
        }
        glColor4f(1f, 1f, 1f, 1f);
        glEnable(GL_TEXTURE_2D);
        TextWriter.renderText(GameClient.quests.getNumberOfActiveQuests() + (GameClient.quests.getNumberOfActiveQuests() == 1 ? " active quest" : " active quests"), GameRenderer.tilesX - 7.8f, GameRenderer.tilesY - 0.5f);
        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
            // Koordinaten verschieben
            glTranslated(GameRenderer.tilesX - 8, GameRenderer.tilesY - 2.5f, 0);
            GameClient.quests.getCurrentQuest().renderVisual();
            glTranslated(-(GameRenderer.tilesX - 8), -(GameRenderer.tilesY - 2.5f), 0);
        }
    }
}
