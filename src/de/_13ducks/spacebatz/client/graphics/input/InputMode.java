package de._13ducks.spacebatz.client.graphics.input;

/**
 * Ein Eingabemodus.
 * Gehört in der Regel zu einem CoreRenderer, und darf Maus- und Tastaturrohdaten auslesen.
 *
 * Das InputSystem kann Tastatur und/oder Maus freezen lassen, wenn ein Overlay das anfordert.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class InputMode {

    /**
     * Wenn true, die Tastatureingaben einfrieren.
     */
    private boolean freezeKeyboard = false;
    /**
     * Wenn true, die Mauseingaben einfrieren.
     */
    private boolean freezeMouseMovement = false;
    /**
     * Wenn true, die Mauseingaben einfrieren.
     */
    private boolean freezeMouseKlicks = false;

    /**
     * Wird vom Inputsystem aufgerufen und verarbeitet den Rohdateninput.
     */
    public final void input() {
        if (!freezeMouseMovement) {
            mouseMovement();
        }
        if (!freezeKeyboard) {
            permanentKeyboardInput();
        }
        if (!freezeMouseKlicks) {
            mouseInput();
        }
    }

    /**
     * Wird vom Inputsystem aufgerufen, um die Mauseingaben einzufrieren (und wieder freizugeben)
     *
     * @param freezeMouseKlicks true zum Einfrieren
     */
    public void setMouseKlickFreeze(boolean freezeMouseKlicks) {
        this.freezeMouseKlicks = freezeMouseKlicks;
    }

    /**
     * Wird vom Inputsystem aufgerufen, um die Mauseingaben einzufrieren (und wieder freizugeben)
     *
     * @param freezeMouseKlicks true zum Einfrieren
     */
    public void setMouseMoveFreeze(boolean freezeMouseMovement) {
        this.freezeMouseMovement = freezeMouseMovement;
    }

    /**
     * Wird vom Inputsystem aufgerufen, um die Tastatureingaben einzufrieren (und wieder freizugeben)
     *
     * @param freezeKeyboard true zum Einfrieren
     */
    public void setKeyboardFreeze(boolean freezeKeyboard) {
        this.freezeKeyboard = freezeKeyboard;
    }

    /**
     * Hier dürfen beliebig Tasten mit Keyboard.isPressed() ausgelesen werden.
     * ES DARF *NICHT* Keyboard.next() aufgerufen werden.
     * Wird synchron zu Frames aufgerufen.
     */
    public abstract void permanentKeyboardInput();

    /**
     * Hier dürfen beliebig Tasten mit Keyboard.isPressed() ausgelesen werden.
     * ES DARF *NICHT* Keyboard.next() aufgerufen werden.
     * Wird asynchron zu Frames, nämlich an Logik-Ticks gebunden aufgerufen.
     *
     * @param mainInputActive Wenn false hat jemand anderes gerade den Fokus auf der Tastatur
     */
    public abstract void permanentAsyncKeyboardInput(boolean mainInputActive);

    /**
     * Hiermit werden alle Tasten-Events weitergeleitet.
     * Kann offensichtlich - im Gegensatz zu den anderen Methoden - mehr als ein Mal pro Tick aufgerufen werden.
     *
     * @param key die gedrückte Taste
     * @param pressed gedrückt (true) oder losgelassen (false)
     */
    public abstract void keyboardPressed(int key, boolean pressed);

    /**
     * Hier werden Mausklicks verarbeitet.
     */
    public abstract void mouseInput();

    /**
     * Hier wird die reine Bewegung der Maus verarbeitet.
     */
    public abstract void mouseMovement();
}
