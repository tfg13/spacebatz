package de._13ducks.spacebatz.util;

/**
 * Ein generischer(!) Ringpuffer, der <size> Elemente eines Typs speichert.
 * Wenn ein Element eingefügt wird das älteste überschrieben.
 * 
 * @author michael
 */
public class RingBuffer<T> {

    private Object[] items;
    private int startIndex;
    private int size;

    public RingBuffer(int size) {
        items = new Object[size];
        this.size = size;
    }

    /**
     * Fügt ein neues Element ein, überschreibt das älteste Element.
     * @param element 
     */
    public void insert(T element) {
        if (items[startIndex] == null) {
            items[startIndex] = element;
        } else {
            startIndex++;
            items[startIndex] = element;
        }
    }

    /**
     * Gibt die Puffergröße zurück.
     * @return 
     */
    public int getBufferSize() {
        return size;
    }

    /**
     * Gibt das index-te element zurück.
     * 0 ist das aktuellste Element.
     * @param index
     * @return 
     */
    public T get(int index) {
        return (T) items[(index + startIndex) % size];
    }
}
