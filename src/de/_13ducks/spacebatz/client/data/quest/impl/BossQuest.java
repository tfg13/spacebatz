package de._13ducks.spacebatz.client.data.quest.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.data.quest.ClientQuest;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import static org.lwjgl.opengl.GL11.*;

/**
 * Boss-besiegen-Quest
 *
 * @author michael
 */
public class BossQuest extends ClientQuest {

    /**
     * Das Ziel, da muss der Spieler sich hinbewegen.
     */
    private final float[] target;

    public BossQuest(int questId, int targetNetId, float targetPositionX, float targetPositionY) {
        super(questId);
        target = new float[]{targetPositionX, targetPositionY};
        
    }

    @Override
    public boolean hasVisual() {
        return true;
    }

    @Override
    public void renderVisual() {
        // Drehen
        glPushMatrix();
        glTranslated(1, 1, 0);
        double dir = Math.atan2(GameClient.player.getX() - target[0], target[1] - GameClient.player.getY());
        glRotated(dir / Math.PI * 180, 0, 0, 1);
        glTranslated(-1, -1, 0);
        RenderUtils.getTextureByName("misc.png").bind();
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(0 * 0.0625f, 0 * 0.0625f); // Obere linke Ecke auf der Tilemap (Werte von 0 bis 1)
        glVertex3f(0, 0 + 2, 0); // Obere linke Ecke auf dem Bildschirm (Werte wie eingestellt (Anzahl ganzer Tiles))
        // Die weiteren 3 Ecken im Uhrzeigersinn:
        glTexCoord2f((0 + 2) * 0.0625f, 0 * 0.0625f);
        glVertex3f(0 + 2, 0 + 2, 0);
        glTexCoord2f((0 + 2) * 0.0625f, (0 + 2) * 0.0625f);
        glVertex3f(0 + 2, 0, 0);
        glTexCoord2f(0 * 0.0625f, (0 + 2) * 0.0625f);
        glVertex3f(0, 0, 0);
        glEnd(); // Zeichnen des QUADs fertig
        glPopMatrix();
    }
}
