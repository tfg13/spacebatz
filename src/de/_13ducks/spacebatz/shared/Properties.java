package de._13ducks.spacebatz.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Verwaltet eine Liste von Eigenschaften.
 * Jede Eigenschaft hat einen string-Namen und einen Double-Wert.
 *
 * @author michael
 */
public class Properties implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Die Liste der Eigenschaften
     */
    private HashMap<String, Double> properties;

    public Properties() {
        properties = new HashMap<>();
    }

    /**
     * Gibt den Wert einer Eigenschaft zurück.
     * Wenn die Eigenschaft nicht initialisiert wurde, wird 0 zurückgegeben.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getProperty(String name) {
        if (properties.containsKey(name)) {
            return properties.get(name);
        } else {
            return 0;
        }
    }

    /**
     * Inkrementiert den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der inkrementiert werden soll
     * @param value der Wert, um den die Eigenschaft inkrementiert werden soll
     */
    final public void incrementProperty(String name, double value) {
        properties.put(name, getProperty(name) + value);
    }

    /**
     * Dekrementiert den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der dekrementiert werden soll
     * @param value der Wert, um den die Eigenschaft dekrementiert werden soll
     */
    final public void decrementProperty(String name, double value) {
        properties.put(name, getProperty(name) - value);
    }

    /**
     * Setzt den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der gesetzt werden soll
     * @param value der Wert, auf den die Eigenschaft gesetzt werden soll
     */
    final public void setProperty(String name, double value) {
        properties.put(name, value);
    }

    /**
     * Addiert alle Eigenschaften eines anderen Properties-Objekts zu diesen hinzu.
     *
     * @param otherProperties
     */
    public void addProperties(Properties otherProperties) {
        Iterator<Entry<String, Double>> iter = otherProperties.getIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            incrementProperty(next.getKey(), next.getValue());
        }
    }

    /**
     * Entfernt zieht alle Eigenschaften eines anderen Properties-Objekts von den Eigenschaften dieser Properties ab.
     *
     * @param otherProperties die Properties die von diesen abgezogen werden
     */
    public void removeProperties(Properties otherProperties) {
        Iterator<Entry<String, Double>> iter = otherProperties.getIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            decrementProperty(next.getKey(), next.getValue());
        }
    }

    /**
     * Gibt einen Iterator über alle Name-Wert-Paare dieser Properties zurück
     *
     * @return ein Iterator über alle Werte
     */
    private Iterator<Entry<String, Double>> getIterator() {
        return properties.entrySet().iterator();
    }
}
