package de._13ducks.spacebatz.client.graphics.input;

import de._13ducks.spacebatz.client.graphics.input.impl.GameInput;
import de._13ducks.spacebatz.client.graphics.input.impl.OverlayInputMode;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Genereller Spiel-Input. (synchron mit Frames)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Input {

    /**
     * Der derzeit aktive, primäre Inputmodus.
     */
    private InputMode input = new GameInput();
    /**
     * Die Listener der Overlays, die sich für Input interessieren.
     */
    private Map<OverlayInputListener, OverlayInputMode> inputOverlays = new ConcurrentHashMap<>();
    /**
     * Der aktuell getriggerte Listener.
     */
    private OverlayInputListener triggeredListener;

    /**
     * Input-Hauptmethode.
     * Muss synchron zu den Frames regelmäßig aufgerufen werden.
     */
    public void syncInput() {
        // Zuerst Mausbewegung
        // Ist derzeit ein Overlay getriggert und fängt die Bewegung ab?
        boolean sendMouseMoveToRest = true;
        if (triggeredListener != null) {
            OverlayInputMode mode = inputOverlays.get(triggeredListener);
            if (mode.mouseMode == OverlayInputMode.MOUSE_MODE_TRIGGER_BLOCKING || mode.mouseMode == OverlayInputMode.MOUSE_MODE_TRIGGER_NONBLOCKING) {
                // Mausbewegungen gehen an dieses Overlay:
                triggeredListener.mousePosition(Mouse.getX(), Mouse.getY());
                // War das exklusiv?
                if (mode.mouseMode == OverlayInputMode.MOUSE_MODE_TRIGGER_BLOCKING) {
                    sendMouseMoveToRest = false;
                }
            }
        }
        // Exklusive Mausbewegungen sind jetzt verarbeitet.
        // Mausbewegung trotzdem an den Rest?
        if (sendMouseMoveToRest) {
            input.mouseMovement();
            // Gibt es ein Overlay das da ist?
            OverlayInputListener l = listenerForPoint(Mouse.getX(), Mouse.getY());
            if (l != null) {
                // Dann senden:
                l.mousePosition(Mouse.getX(), Mouse.getY());
            }
        }

        // Jetzt Tastatureingaben verarbeiten:
        while (Keyboard.next()) {
            int key = Keyboard.getEventKey();
            boolean pressed = Keyboard.getEventKeyState();
            boolean sendInputToRest = true;
            // Ist aktuell jemand getriggert?
            if (triggeredListener != null) {
                OverlayInputMode mode = inputOverlays.get(triggeredListener);
                // Interessiert sich der für KeyboardEvents?
                if (mode.keyboardMode == OverlayInputMode.KEYBOARD_MODE_TRIGGER) {
                    sendInputToRest = false;
                    triggeredListener.keyboardInput(key, pressed);
                }
            } else {
                // Es ist zur Zeit niemand getriggert, ist das eine neue Triggertaste?
                if (pressed) {
                    for (OverlayInputListener listener : inputOverlays.keySet()) {
                        OverlayInputMode mode = inputOverlays.get(listener);
                        if (mode.triggerKeys.contains(key)) {
                            // Trigger!
                            sendInputToRest = false;
                            // Self-Untrigger vorbereiten
                            listener.internalTrigger(new Runnable() {
                                @Override
                                public void run() {
                                    // De-Trigger
                                    triggeredListener.internalUntrigger();
                                    triggeredListener = null;
                                }
                            });
                            triggeredListener = listener;
                        }
                        // Es kann nur einer getriggert werden
                        break;
                    }
                }
            }
            // Jetzt sind die exklusiven und die Trigger-Events fertig, kriegt noch jemand den Input?
            if (sendInputToRest) {
                // Hauptmodul
                input.keyboardPressed(key, pressed);
                // Alle passiven:
                for (OverlayInputListener listener : inputOverlays.keySet()) {
                    OverlayInputMode mode = inputOverlays.get(listener);
                    if (mode.keyboardMode == OverlayInputMode.KEYBOARD_MODE_PASSIVE) {
                        listener.keyboardInput(key, pressed);
                    }
                }
            }
        }

        // Jetzt Mausklicks verarbeiten, falls gerade einer ist:
        for (int i = 0; i < CompileTimeParameters.CLIENT_MAX_MOUSE_BUTTONS; i++) {
            if (Mouse.isButtonDown(i)) {
                boolean sendMouseKlickToRest = true;
                // Jemand exklusiv getriggert?
                if (triggeredListener != null) {
                    OverlayInputMode mode = inputOverlays.get(triggeredListener);
                    if (mode.mouseMode == OverlayInputMode.MOUSE_MODE_TRIGGER_BLOCKING || mode.mouseMode == OverlayInputMode.MOUSE_MODE_TRIGGER_NONBLOCKING || mode.mouseMode == OverlayInputMode.MOUSE_MODE_HOVER_NONBLOCKING) {
                        // Trigger bekommt Mausklick:
                        triggeredListener.mouseDown(Mouse.getX(), Mouse.getY(), i);
                        sendMouseKlickToRest = false;
                    }
                } else {
                    int mx = Mouse.getX();
                    int my = Mouse.getY();
                    // Wird jemandem in eine Trigger-Zone geklickt?
                    OUTER:
                    for (OverlayInputListener listener : inputOverlays.keySet()) {
                        OverlayInputMode mode = inputOverlays.get(listener);
                        for (int[] zone : mode.triggerZones) {
                            if (zone[0] <= mx && zone[3] >= mx && zone[1] <= my && zone[3] >= my) {
                                // Trigger-Zone getroffen
                                sendMouseKlickToRest = false;
                                // Self-Untrigger vorbereiten
                                listener.internalTrigger(new Runnable() {
                                    @Override
                                    public void run() {
                                        // De-Trigger
                                        triggeredListener.internalUntrigger();
                                        triggeredListener = null;
                                    }
                                });
                                triggeredListener = listener;
                                break OUTER;
                            }
                        }
                    }
                }

                // Exklusive Maus-Eingaben sind verarbeitet, gibt es noch was für den Rest?
                if (sendMouseKlickToRest) {
                    // Gibt es ein Overlay, das den Klick will?
                    OverlayInputListener l = listenerForPoint(Mouse.getX(), Mouse.getY());
                    if (l != null && inputOverlays.get(l).mouseMode == OverlayInputMode.MOUSE_MODE_HOVER_NONBLOCKING) {
                        // Ja, Mausklick da hin:
                        l.mouseDown(Mouse.getX(), Mouse.getY(), i);
                    } else {
                        // Mausklick an das Haupt-Inputmodul
                        input.mouseInput();
                    }
                }
            }
        }
    }

    /**
     * Input-Hauptmethode für an Logik-Ticks gebundene Aufgaben.
     * Nur Keyboard, und das nur an das Haupt-Modul, falls nichts getriggert.
     */
    public void asyncInput() {
        // Keyboard async ans Hauptmodul
        input.permanentAsyncKeyboardInput(triggeredListener == null || inputOverlays.get(triggeredListener).keyboardMode != OverlayInputMode.KEYBOARD_MODE_TRIGGER);
    }

    /**
     * Sucht unter den registrierten Listenern nach einem, der sich für den gegebenen Bereich interessiert.
     *
     * @param px X-Position der Maus
     * @param py Y-Position der Maus
     * @return der Erste, der sich dafür interessiert, oder null
     */
    private OverlayInputListener listenerForPoint(int px, int py) {
        for (OverlayInputListener listener : inputOverlays.keySet()) {
            OverlayInputMode mode = inputOverlays.get(listener);
            if (listener.getCatchX1() != -1) {
                // Hat eine Input-Zone:
                if (listener.getCatchX1() <= px && listener.getCatchX2() >= px && listener.getCatchY1() <= py && listener.getCatchY2() >= py) {
                    // drin
                    if (mode.mouseMode == OverlayInputMode.MOUSE_MODE_HOVER_NONBLOCKING) {
                        return listener;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Registriert einen neuen OverlayInputListener mit dem gegebenen Modus.
     * Spätestens ab dem nächsten Tick bekommt das Overlay Input wie angefordert.
     */
    public void registerOverlayInputListener(OverlayInputListener listener, OverlayInputMode mode) {
        inputOverlays.put(listener, mode);
    }
}
