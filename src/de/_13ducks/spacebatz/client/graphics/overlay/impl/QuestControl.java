package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.graphics.overlay.Overlay;

/**
 * Zeigt Infos über Quests.
 * Anzahl verfügbarer/aktiver Quests, aktuellen Primärquest
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class QuestControl extends Overlay {

    @Override
    public void render() {
//        glDisable(GL_TEXTURE_2D);
//        glColor4f(.9f, .9f, .9f, .7f);
//        // Quest-Infos anzeigen?
//        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
//            glRectf(LegacyRenderer.tilesX - 8, LegacyRenderer.tilesY, LegacyRenderer.tilesX, LegacyRenderer.tilesY - 2.5f);
//        } else {
//            glRectf(LegacyRenderer.tilesX - 8, LegacyRenderer.tilesY, LegacyRenderer.tilesX, LegacyRenderer.tilesY - 0.5f);
//        }
//        glColor4f(1f, 1f, 1f, 1f);
//        glEnable(GL_TEXTURE_2D);
//        TextWriter.renderText(GameClient.quests.getNumberOfActiveQuests() + (GameClient.quests.getNumberOfActiveQuests() == 1 ? " active quest" : " active quests"), LegacyRenderer.tilesX - 7.8f, LegacyRenderer.tilesY - 0.5f);
//        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
//            // Koordinaten verschieben
//            glTranslated(LegacyRenderer.tilesX - 8, LegacyRenderer.tilesY - 2.5f, 0);
//            GameClient.quests.getCurrentQuest().renderVisual();
//            glTranslated(-(LegacyRenderer.tilesX - 8), -(LegacyRenderer.tilesY - 2.5f), 0);
//        }
    }
}
