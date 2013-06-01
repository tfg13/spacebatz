package de._13ducks.spacebatz.util;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

/**
 * Hilfsklasse, um Floatbuffer zu bauen.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public final class FloatBufferBuilder {

    /**
     * Die eigentlichen Daten.
     */
    private float[] data;
    /**
     * Aktueller Index
     */
    private int index = 0;

    /**
     * Erzeugt einen neuen FloatBufferBuilder mit der angegebenen Maximalgröße
     *
     * @param maxSize die Maximalgröße
     */
    public FloatBufferBuilder(int maxSize) {
        data = new float[maxSize];
    }

    /**
     * Schreibt den gegebenen Wert an die nächste Position in den BufferBuilder
     *
     * @param f der neue Wert
     * @return this, zum verketten
     */
    public FloatBufferBuilder put(float f) {
        data[index++] = f;
        return this;
    }

    /**
     * Liefert die Anzahl der Elemente, die bereits hinzugefügt wurden.
     *
     * @return Größe des FloatBufferBuilders
     */
    public int size() {
        return index;
    }

    /**
     * Bildet aus den bisherigen Daten einen echten, nativen Floatbuffer.
     * Dessen Größe entspricht gerade den bisher eingefügten Elementen.
     * Der gelieferte Buffer ist bereits geflippt.
     *
     * @return richtigen Floatbuffer
     */
    public FloatBuffer toBuffer() {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(index);
        for (int i = 0; i < index; i++) {
            buffer.put(data[i]);
        }
        buffer.flip();
        return buffer;
    }
}
