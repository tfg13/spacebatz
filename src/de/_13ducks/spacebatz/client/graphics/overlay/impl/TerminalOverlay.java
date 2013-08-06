package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.overlay.TriggeredOverlay;
import de._13ducks.spacebatz.client.graphics.vao.VAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import org.lwjgl.input.Keyboard;

/**
 * Overlay für das Client-Terminal.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class TerminalOverlay extends TriggeredOverlay {

    private VAO background;

    public TerminalOverlay() {
        float[] grey = new float[]{.9f, .9f, .9f, .7f};
        background = VAOFactory.createStaticColoredRectVAO(1);
        background.pushRectC(DefaultSettings.CLIENT_GFX_RES_X / 3, 0, 2 * DefaultSettings.CLIENT_GFX_RES_X / 3, DefaultSettings.CLIENT_GFX_RES_Y / 2, grey, grey, grey, grey);
        background.upload();
    }

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
        background.render();
        float rowheight = DefaultSettings.CLIENT_GFX_RES_Y / 40f;
        TextWriter.renderText(GameClient.terminal.getCurrentLine(), DefaultSettings.CLIENT_GFX_RES_X / 3 + 5, rowheight, true);
        int numberoflines = 20;//(int) ((int) tilesY * GameClient.getEngine().getGraphics().defactoRenderer().getZoomFact() / 2);
        for (int i = 0; i < numberoflines - 1; i++) {
            TextWriter.renderText(GameClient.terminal.getHistory(i), DefaultSettings.CLIENT_GFX_RES_X / 3 + 5, (i + 2) * rowheight, true);
        }
    }
}
