package de._13ducks.spacebatz.client.graphics;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Ein Steuerelement, das gerendert wird und Eingaben verarbeiten kann.
 *
 * @author michael
 */
public abstract class Control {

    private boolean active;

    public Control() {
        active = false;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Rendert dieses Control.
     */
    public abstract void render(Camera camera, TextWriter textWriter);

    /**
     * Verarbeitet Eingaben.
     *
     * @param mouse
     * @param keyboard
     */
    public abstract void input();

    /**
     * Initialisiert das COntrol.
     */
    public abstract void initialise();
}
