package de._13ducks.spacebatz.client.graphics;

/**
 * Ein Steuerelement, das gerendert wird und Eingaben verarbeiten kann.
 *
 * @author michael
 */
public interface Control {

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
