package de._13ducks.spacebatz.client.graphics;

/**
 * Ein Steuerelement, das gerendert wird und Eingaben verarbeiten kann.
 *
 * @author michael
 */
public abstract class Control {

    /**
     * Gibt an ob das Control aktiv ist.
     */
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
    public abstract void render(Renderer renderer);

    /**
     * Verarbeitet Eingaben.
     *
     * @param mouse
     * @param keyboard
     */
    public abstract void input();
}
