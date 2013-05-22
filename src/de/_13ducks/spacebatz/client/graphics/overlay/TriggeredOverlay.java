package de._13ducks.spacebatz.client.graphics.overlay;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.input.OverlayInputListener;
import de._13ducks.spacebatz.client.graphics.input.impl.OverlayInputMode;
import org.lwjgl.input.Keyboard;

/**
 * Eine Klasse um simpel Overlays zu bauen, die sich beim druck einer bestimmten Taste einblenden.
 *
 * In der init-Methode können beliebig viele Tasten mitgegeben werden, auf die das Overlay reagiert.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class TriggeredOverlay extends Overlay {

    private boolean triggered = false;

    /**
     * Initialisiert das Overlay mit den gegebenen Triggertasten.
     *
     * @param triggerKeys Die Tasten, bei denen das Overlay anspringt. Darf ESC nicht enthalten.
     * @param closeWithSameKeys Wenn true, geht das Overlay bei den selben Keys auch wieder zu. Wenn false, geht es nur mit ESC zu.
     */
    public void init(final int[] triggerKeys, final boolean closeWithSameKeys) {
        // Check for ESC
        for (int key : triggerKeys) {
            if (key == Keyboard.KEY_ESCAPE) {
                throw new IllegalArgumentException("Overlays cannot catch ESC");
            }
        }

        OverlayInputMode mode = new OverlayInputMode(OverlayInputMode.KEYBOARD_MODE_TRIGGER, OverlayInputMode.MOUSE_MODE_TRIGGER_BLOCKING);
        for (int key : triggerKeys) {
            mode.triggerKeys.add(key);
        }

        GameClient.getEngine().getGraphics().getInput().registerOverlayInputListener(new OverlayInputListener() {
            @Override
            public void triggerChanged() {
                triggered = this.isTriggered();
            }

            @Override
            public void keyboardInput(int key, boolean pressed) {
                // Abschalt-Trigger?
                if (closeWithSameKeys) {
                    for (int tkey : triggerKeys) {
                        if (tkey == key) {
                            untrigger();
                            return;
                        }
                    }
                }
                // Kein detrigger, dann weiterleiten:
                TriggeredOverlay.this.keyboardInput(key, pressed);
            }

            @Override
            public void mouseMove(int mx, int my) {
                TriggeredOverlay.this.mouseMove(mx, my);
            }

            @Override
            public void mouseDown(int mx, int my, int button) {
                TriggeredOverlay.this.mouseDown(mx, my, button);
            }

            @Override
            public int getCatchX1() {
                return -1;
            }

            @Override
            public int getCatchY1() {
                return -1;
            }

            @Override
            public int getCatchX2() {
                return -1;
            }

            @Override
            public int getCatchY2() {
                return -1;
            }
        }, mode);
    }

    @Override
    public void render() {
        if (triggered) {
            triggeredRender();
        }
    }

    /**
     * Siehe OverlayInputListener.keyboardInput(int, boolean)
     */
    protected abstract void keyboardInput(int key, boolean pressed);

    /**
     * Siehe OverlayInputListener.mouseMove(int, int)
     */
    protected abstract void mouseMove(int mx, int my);

    /**
     * Siehe OverlayInputListener.mouseDown(int, int, boolean)
     */
    protected abstract void mouseDown(int mx, int my, int button);

    /**
     * Siehe Overlay.render()
     */
    protected abstract void triggeredRender();
}
