package de._13ducks.spacebatz.shared.network;

/**
 * Ein Ringpuffer für int-Werte der Statistik-Werte wie den Durchschnitt effizient berechnen kann.
 * Alle Operationen dieses Puffers laufen in O(1) insbesondere auch die Berechnung des Durchschnitts, egal wie groß der Puffer ist.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class StatisticRingBuffer {

    /**
     * Der tatsächliche Puffer.
     */
    private int[] buffer;
    /**
     * Der Index des Eintrags, der als nächstes Überschrieben wird.
     */
    private int index = 0;
    /**
     * Aktueller Durchschnitt.
     * Wird immer beim Einfügen aktualisiert.
     */
    private double avg;

    /**
     * Erzeugt einen neuen Ringpuffer mit der gegebenen Größe.
     *
     * @param size die gewünschte Größe
     */
    public StatisticRingBuffer(int size) {
        buffer = new int[size];
    }

    /**
     * Schreibt einen Wert in den Ringpuffer.
     * Überschreibt automatisch den ältesten Wert.
     *
     * @param value der neue Wert
     */
    public void push(int value) {
        // Durchschnitt updaten:
        avg -= 1.0 * buffer[index] / buffer.length;
        avg += 1.0 * value / buffer.length;
        // Normales Einfügen in den Ringpuffer
        buffer[index++] = value;
        if (index == buffer.length) {
            index = 0;
        }
    }

    /**
     * Liefert den Durchschnitt aller gespeicherten Werte.
     *
     * @return den Durchschnitt aller gespeicherten Werte
     */
    public double getAvg() {
        return avg;
    }
}
