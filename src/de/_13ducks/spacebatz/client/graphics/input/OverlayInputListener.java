package de._13ducks.spacebatz.client.graphics.input;

/**
 * Ein Listener, mit dem Overlays über Input informiert werden.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public abstract class OverlayInputListener {
    
    /**
     * True, während der InputListener getriggert ist.
     */
    private boolean triggered = false;
    /**
     * Dieser Listener kommt vom Inputsystem und erlaubt es uns, uns selbst zu detriggern.
     * Nur gesetzt, während triggered = true;
     */
    private Runnable detriggerListener;
    
    /**
     * Liefert true, wenn dieser Listener derzeit getriggert ist.
     * @return true, wenn getriggert
     */
    public final boolean isTriggered() {
        return triggered;
    }
    
    /**
     * Triggert diesen Listener.
     * Wird nur intern vom Inputsystem verwendet.
     * @param detriggerListener Listener des Inputsystems, mit dem sich dieser Listener selbst untriggern kann
     */
    final void internalTrigger(Runnable detriggerListener) {
        if (!triggered) {
            triggered = true;
            this.detriggerListener = detriggerListener;
            triggerChanged();
        }
    }
    
    /**
     * De-Triggert diesen Listener.
     * Wird nur intern vom Inputsystem verwendet.
     */
    final void internalUntrigger() {
        if (triggered) {
            triggered = false;
            this.detriggerListener = null;
            triggerChanged();
        }
    }
    
    /**
     * Detriggert diesen Listener manuell.
     * 
     */
    public final void untrigger() {
        if (!isTriggered()) {
            throw new IllegalStateException("OverlayInputListener cannot be untriggered: Not triggered!");
        }
        detriggerListener.run();
    }
    
    /**
     * Wird aufgerufen, wenn sich der trigger-Zustand geändert hat.
     * Der tatsächliche Zustand kann mit isTriggered() herausgefunden werden.
     * 
     * Diese Methode gibt der Implementierung die Chance, auf dieses Event zu reagieren.
     */
    public abstract void triggerChanged();
    
    /**
     * Wird aufgerufen, wenn Tastatureingaben für diese Overlay vorliegen.
     * Welche Tastatureingaben relevant sind, kommt darauf an, mit welchen Einstellungen dieser Listener beim Inputsystem registriert wurde.
     * 
     * Tastatur-Input ist an Grafik-Frames gebunden, wird synchron zur Grafik aufgerufen (muss also *sehr* schnell verarbeitet werden),
     * und kann mehrfach pro Frame aufgerufen werden (wenn mehrere Events vorliegen)
     * 
     * @param key Die Event-Taste
     * @param pressed Gedrückt (true) oder losgelassen (false)
     */
    public abstract void keyboardInput(int key, boolean pressed);
    
    /**
     * Wird aufgerufen, wenn Mauseingaben für dieses Overlay vorliegen.
     * Welche Mauseingaben relevant sind, kommmt darauf an, mit welchen Einstellungen dieser Listener beim Inputsystem registriert wurde.
     * 
     * Dieser Maus-Input ist an Frames gebunden, wird synchron zur Grafik aufgerufen (muss also *sehr* schnell verarbeitet werden),
     * und wird pro Frame maximal ein Mal aufgerufen.
     * 
     * @param mx Mausposition in X
     * @param my Mausposition in Y
     */
    public abstract void mousePosition(int mx, int my);
    
    /**
     * Wird aufgerufen, wenn Mauseingaben für dieses Overlay vorliegen.
     * Welche Mauseingaben relevant sind, kommt darauf an, mit welchen Einstellungen dieser Listener beim Inputsystem registriert wurde.
     * 
     * Dieser Maus-Input ist an Frames gebunden, wird sychron zu Grafik aufgerufen (muss also *sehr* schnell verarbeitet werden),
     * und wird pro Frame maximal ein Mal pro Button aufgerufen.
     * @param mx
     * @param my
     * @param button 
     */
    public abstract void mouseDown(int mx, int my, int button);
    
    /**
     * Liefert die erste X-Koordinate der Input-Zone dieses Overlay-Listeners.
     * -1, wenn keine Inputzone verwendet wird.
     * @return X1 der Inputzone, oder -1
     */
    public abstract int getCatchX1();
    /**
     * Liefert die erste Y-Koordinate der Input-Zone dieses Overlay-Listeners.
     * @return Y1 der Inputzone
     */
    public abstract int getCatchY1();
    /**
     * Liefert die zweite X-Koordinate der Input-Zone dieses Overlay-Listeners.
     * @return X2 der Inputzone
     */
    public abstract int getCatchX2();
    /**
     * Liefert die zweite Y-Koordinate der Input-Zone dieses Overlay-Listeners.
     * @return Y2 der Inputzone
     */
    public abstract int getCatchY2();
    

}
