package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import static org.lwjgl.opengl.GL11.*;

/**
 * Zeigt Infos über Quests.
 * Anzahl verfügbarer/aktiver Quests, aktuellen Primärquest
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class QuestControl implements Control {

    @Override
    public void render(Renderer renderer) {
        Camera camera = renderer.getCamera();
        TextWriter textWriter = renderer.getTextWriter();
        glDisable(GL_TEXTURE_2D);
        glColor4f(.9f, .9f, .9f, .7f);
        // Quest-Infos anzeigen?
        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
            glRectf(camera.getTilesX() - 8, camera.getTilesY(), camera.getTilesX(), camera.getTilesY() - 2.5f);
        } else {
            glRectf(camera.getTilesX() - 8, camera.getTilesY(), camera.getTilesX(), camera.getTilesY() - 0.5f);
        }
        glColor4f(1f, 1f, 1f, 1f);
        glEnable(GL_TEXTURE_2D);
        textWriter.renderText(GameClient.quests.getNumberOfActiveQuests() + (GameClient.quests.getNumberOfActiveQuests() == 1 ? " active quest" : " active quests"), camera.getTilesX() - 7.8f, camera.getTilesY() - 0.5f);
        if (GameClient.quests.getNumberOfActiveQuests() > 0 && GameClient.quests.getCurrentQuest().hasVisual()) {
            // Koordinaten verschieben
            glTranslated(camera.getTilesX() - 8, camera.getTilesY() - 2.5f, 0);
            GameClient.quests.getCurrentQuest().renderVisual(renderer);
            glTranslated(-(camera.getTilesX() - 8), -(camera.getTilesY() - 2.5f), 0);
        }
    }

    @Override
    public void input() {
        // Ignore
    }
}
