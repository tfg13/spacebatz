package de._13ducks.spacebatz.client.graphics.util;

/**
 * Ein Button, der Mausklicks empfängt und reagiert wenn er angeklickt wurde.
 *
 * @author mekhar
 */
public abstract class Button extends VisibleGUIElement {

    /**
     * Position und Maße in Pixeln.
     */
    private int posX, posY, width, height;
    /**
     * Gibt an, ob die linke Maustaste über diesem Element gedrückt wurde. Nur gültig wenn isMouseButtonDown gesetzt ist.
     */
    private boolean wasMousePressedOverThis;

    public Button(int posX, int posY, int width, int height) {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void mouseMove(int mx, int my, int button) {
    }

    @Override
    public final void mousePressed(float x, float y, int button) {
        onMouseButtonPressed();
        if (isMouseOver(x, y)) {
            wasMousePressedOverThis = true;
        } else {
            wasMousePressedOverThis = false;
        }
    }

    @Override
    public final void mouseReleased(float x, float y, int button) {
        onMouseButtonReleased();
        if (wasMousePressedOverThis && isMouseOver(x, y)) {
            onClick();
            wasMousePressedOverThis = false;
        }
    }

    /**
     * Wird immer aufgeruden wenn die Maustaste irgendwo gedrückt wurde.
     */
    public abstract void onMouseButtonPressed();

    /**
     * Wird immer aufgerufen wenn die linke Maustaste irgendwo losgelassen wurde.
     */
    public abstract void onMouseButtonReleased();

    /**
     * Wird aufgerufen, wenn die linke Maustaste über diesem Controlelement gedrückt und wieder losgelassen wurde.
     */
    public abstract void onClick();

    /**
     * Gibt an ob die Maus über diesem Controlelement ist.
     *
     * @param mouseX X-Koordinate der Maus (von 0.0f bis 1.0f)
     * @param mouseY Y-Koordinate der Maus (von 0.0f bis 1.0f)
     * @return
     */
    private boolean isMouseOver(float mouseX, float mouseY) {
        return posX < mouseX && mouseX < (posX + width) && posY < mouseY && mouseY < (posY + height);
    }

    @Override
    public abstract void keyboardInput(int key, boolean down);
}
