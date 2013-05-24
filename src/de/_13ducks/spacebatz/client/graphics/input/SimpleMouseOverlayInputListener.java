package de._13ducks.spacebatz.client.graphics.input;

import de._13ducks.spacebatz.shared.CompileTimeParameters;

/**
 * Wandelt die etwas unhandlichen Maus-Inputs des OverlayInputListeners in schöne Events um.
 *
 * Achtung: Benötigt Ansteuerung aus der render()-Methode des Overlays!!!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class SimpleMouseOverlayInputListener extends OverlayInputListener {

    private int lastMouseStates[] = new int[CompileTimeParameters.CLIENT_MAX_MOUSE_BUTTONS];
    private int mouseX = -1;
    private int mouseY = -1;

    @Override
    public void mousePosition(int mx, int my) {
        if (mouseX != mx || mouseY != my) {
            mouseX = mx;
            mouseY = my;
            mouseMove(mx, my);
        }
    }

    @Override
    public void mouseDown(int mx, int my, int button) {
        if (lastMouseStates[button] == 0) {
            mousePressed(mx, my, button);
            lastMouseStates[button] = 2;
        } else if (lastMouseStates[button] == 1) {
            lastMouseStates[button]++;
        } else {
            throw new RuntimeException("Wrong usage of SimpleMouseOverlayInputListener, you MUST call manageInput() regularly");
        }
    }

    /**
     * Diese Methode MUSS aus der render()-Methode des Overlays aufgerufen werden!
     * Und zwar jedes Mal, sonst funktioniert diese Hilfsklasse nicht!
     */
    public void manageInput() {
        for (int i = 0; i < CompileTimeParameters.CLIENT_MAX_MOUSE_BUTTONS; i++) {
            if (lastMouseStates[i] > 0) {
                if (--lastMouseStates[i] == 0) {
                    // Nicht mehr gedrückt:
                    mouseReleased(mouseX, mouseY, i);
                }
            }
        }
    }

    /**
     * Wird aufgerufen, wenn ein Mausbutton heruntergedrückt wurde.
     * Wird im Gegensatz zu den Methoden aus dem OverlayInputListener nur ein mal aufgerufen.
     *
     * @param mx X-Koordinate
     * @param my Y-Koordinate
     * @param button der Button
     */
    public abstract void mousePressed(int mx, int my, int button);

    /**
     * Wird aufgerufen, wenn ein Mausbutton losgelassen wurde.
     * Wird im Gegensatz zu den Methoden aus dem OverlayInputListener nur ein mal aufgerufen.
     *
     * @param mx X-Koordinate
     * @param my Y-Koordinate
     * @param button der Button
     */
    public abstract void mouseReleased(int mx, int my, int button);

    /**
     * Wird aufgerufen, wenn sich die Mausposition geändert hat.
     *
     * @param mx X-Koordinate
     * @param my Y-Koordinate
     */
    public abstract void mouseMove(int mx, int my);
}
