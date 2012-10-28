package de._13ducks.spacebatz.client.graphics;

import org.lwjgl.input.Mouse;

/**
 * Ein Kontrollelement. Wird gerendert und kann Eingaben verarbeiten.
 *
 * @author michael
 */
public abstract class ControlElement {

    /**
     * X-Koordinate in % des Bildschirms (von 0.0f bis 1.0f)
     */
    private float x;
    /**
     * Y-Koordinate in % des Bildschirms (von 0.0f bis 1.0f)
     */
    private float y;
    /**
     * Breite in % des Bildschirms (von 0.0f bis 1.0f)
     */
    private float width;
    /**
     * Höhe in % des Bildschirms (von 0.0f bis 1.0f)
     */
    private float height;
    /**
     * Gibt an ob die linke Maustaste gedrückt ist.
     */
    private boolean isMouseButtonDown;
    /**
     * Gibt an, ob die linke Maustaste über diesem Element gedrückt wurde.
     * Nur gültig wenn isMouseButtonDown gesetzt ist.
     */
    private boolean wasMousePressedOverThis;

    public ControlElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Standardkonstruktor, erzeugt ein ControlElement ohne Maße.
     */
    public ControlElement() {
    }

    /**
     * Rendert dieses Controlelement.
     */
    public abstract void render(Renderer renderer);

    /**
     * Berechnet Benutzereingaben für dieses Controlelement.
     */
    public void input(float mouseX, float mouseY) {
        if (Mouse.isButtonDown(0)) {
            if (!isMouseButtonDown) {
                isMouseButtonDown = true;
                onMouseButtonPressed();
                if (isMouseOver(mouseX, mouseY)) {
                    wasMousePressedOverThis = true;
                } else {
                    wasMousePressedOverThis = false;
                }
            }
        } else {
            if (isMouseButtonDown) {
                isMouseButtonDown = false;
                onMouseButtonReleased();
                if (wasMousePressedOverThis && isMouseOver(mouseX, mouseY)) {
                    onClick();
                    wasMousePressedOverThis = false;
                }
            }
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
    public boolean isMouseOver(float mouseX, float mouseY) {
        return x < mouseX && mouseX < (x + width) && y < mouseY && mouseY < (y + height);
    }

    /**
     * Setzt Position und Maße dieses Controls.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setGeometry(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(float height) {
        this.height = height;
    }
}
