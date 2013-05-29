package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.overlay.TriggeredOverlay;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.tilesX;
import static de._13ducks.spacebatz.client.graphics.renderer.impl.LegacyRenderer.tilesY;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glRectf;

/**
 * Overlay für das Client-Terminal.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class TerminalOverlay extends TriggeredOverlay {

    @Override
    protected void keyboardInput(int key, boolean pressed) {
        if (key == Keyboard.KEY_RETURN || key == Keyboard.KEY_NUMPADENTER) {
            GameClient.terminal.enter();
        } else if (key == Keyboard.KEY_BACK) {
            GameClient.terminal.backspace();
        } else if (key == Keyboard.KEY_UP) {
            GameClient.terminal.scrollBack();
        } else if (key == Keyboard.KEY_DOWN) {
            GameClient.terminal.scrollForward();
        } else {
            char c = Keyboard.getEventCharacter();
            if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= ' ' && c <= '?')) {
                GameClient.terminal.input(c);
            }
        }
    }

    @Override
    protected void mouseMove(int mx, int my) {
    }

    @Override
    protected void mousePressed(int mx, int my, int button) {
    }

    @Override
    protected void mouseReleased(int mx, int my, int button) {
    }

    @Override
    protected void triggeredRender() {
        glDisable(GL_TEXTURE_2D);
        glColor4f(.9f, .9f, .9f, .7f);
        glRectf(tilesX / 3, tilesY / 2, tilesX, 0);
        glColor4f(1f, 1f, 1f, 1f);
        glEnable(GL_TEXTURE_2D);
        TextWriter.renderText(GameClient.terminal.getCurrentLine(), tilesX / 3 + 0.5f, 0, true);
        int numberoflines = (int) ((int) tilesY * GameClient.getEngine().getGraphics().defactoRenderer().getZoomFact() / 2);
        for (int i = 0; i < numberoflines - 1; i++) {
            TextWriter.renderText(GameClient.terminal.getHistory(i), tilesX / 3 + 0.5f, tilesY * ((i + 1) / (float) numberoflines / 2.0f), true);
        }
        glColor4f(1f, 1f, 1f, 1f);
    }
}
