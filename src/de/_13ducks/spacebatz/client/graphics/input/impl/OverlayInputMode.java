package de._13ducks.spacebatz.client.graphics.input.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Beschreibt, für was für Arten von Input sich das Overlay interessiert und wann es Input bekommt.
 * 
 * Overlays können beliebig viele Tasten und Bildschrimbereiche als Trigger registrieren lassen.
 * Wenn eine solche Taste oder Bildschrimbereich gedrückt/angeklickt wird, wird das Inputsystem das Overlay benachrichtigen und ihm Vollzugriff auf Tastatur und Maus geben.
 * 
 * Ein OverlayInputListener ist entweder getriggert oder nicht, es wird nicht zwischen Trigger für Tastatur- und Mauseingaben unterschieden.
 * 
 * Diese Konstruktor(en) und Methoden dieser Klasse führen keinen Test durch, ob die Parameter sinnvoll sind.
 * Das Verhalten des Inputsystems, bei definierten Triggern ohne Triggeraktionen oder undefinierten Triggern ohne Triggeraktionen ist undefiniert.
 * 
 * Diese Klasse hat praktisch kein Verhalten und speichert nur Informationen. (*hust* Scala-structs *hust*)
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class OverlayInputMode {
    
    /**
     * Niemals Tastatureingaben auslesen.
     */
    public static final int KEYBOARD_MODE_NEVER = 0;
    /**
     * Overlay bekommt exklusiv (!) alle Tastatureingaben, sobald eine Triggertaste gedrückt wurde.
     */
    public static final int KEYBOARD_MODE_TRIGGER = 1;
    /**
     * Overlay liest immer passiv die Tastatureingaben mit, hat aber keinen Exklusivzugriff.
     */
    public static final int KEYBOARD_MODE_PASSIVE = 2;
    
    /**
     * Niemals Mauseingaben auslesen, niemals Mauseingaben für Haupt-Input blockieren.
     */
    public static final int MOUSE_MODE_NEVER = 10;
    /**
     * Overlay bekommt Mauseingaben, während die Maus über dem Overlay schwebt.
     * Mausbewegungen (nur die) gehen gleichzeigt aber weiter an den Haupt-Input.
     */
    public static final int MOUSE_MODE_HOVER_NONBLOCKING = 11;
    /**
     * Overlay bekommt alle Mauseingaben, nachdem es getriggert wurde.
     * Mausbewegungen (nur die) gehen gleichzeitig aber weiter an den Rest.
     */
    public static final int MOUSE_MODE_TRIGGER_NONBLOCKING = 12;
    /**
     * Overlay bekommt alle Mauseingaben exklusiv, nachdem es getriggert wurde.
     */
    public static final int MOUSE_MODE_TRIGGER_BLOCKING = 13;
    
    /**
     * Der gewählte Tastatur-Eingabemodus.
     */
    public final int keyboardMode;
    /**
     * Der gewählte Maus-Eingabemodus.
     */
    public final int mouseMode;
    /**
     * Tastatureingaben, die diesen Modus triggern.
     */
    public final List<Integer> triggerKeys = new ArrayList<>();
    /**
     * Bereiche auf dem Bildschrim, die diesen Modus triggern.
     * Angaben in Pixeln, die Arrays müssen 4 Einträge haben: X1, Y1, X2, Y2
     */
    public final List<int[]> triggerZones = new ArrayList<>();
    
    /**
     * Erzeugt einen neuen Overlay-Eingabemodus mit den gegebenen Parametern.
     * @param keyboardMode
     * @param mouseMode 
     */
    public OverlayInputMode(int keyboardMode, int mouseMode) {
        this.keyboardMode = keyboardMode;
        this.mouseMode = mouseMode;
    }
}
