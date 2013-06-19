package de._13ducks.spacebatz.client.graphics.util;

/**
 * Ein GUIElement mit Position und Größe. Verarbeitet Eingaben und erkennt, wenn es angeklickt wurde.
 *
 * @author mekhar
 */
public abstract class GUIElement {

    /**
     * Gibt an ob dieses Element gerender wird.
     */
    public boolean isVisible = true;

    /**
     * Rendert dieses Element, wenn es sichtbar ist.
     */
    public final void render() {
        if (isVisible) {
            renderElement();
        }
    }

    /**
     * Wird aufgerufen wenn dieses Element sichtbar ist und gerendert werden soll. Wird von Unterklassen überschrieben, wenn sie etwas render wollen.
     */
    public abstract void renderElement();

    public abstract void mouseMove(int mx, int my, int button);

    public abstract void mousePressed(float x, float y, int button);

    public abstract void mouseReleased(float x, float y, int button);

    public abstract void keyboardInput(int key, boolean down);
}
