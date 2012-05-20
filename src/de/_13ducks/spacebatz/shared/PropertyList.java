package de._13ducks.spacebatz.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Verwaltet eine Liste von Eigenschaften. Jede Eigenschaft hat einen string-Namen, einen Grundwert und einen
 * Multiplikator. Der Wert einer Eigenschaft ergibt sich aus dem Grundwert mal dem Multiplikator. Nicht gesetzte
 * Eigenschaften werden als 0 interpretiert, nicht gesetzte Multiplikatoren asl 1.0 .
 *
 * Beim Addieren/subtrahieren anderer PropertyLists zu/von dieser PropertyList werden Grundwerte zu Grundwerten und
 * Multiplikatoren zu Multiplikatoren addiert/subtrahiert.
 *
 * @author michael
 */
public class PropertyList implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Die Liste der Grundwerte
     */
    private HashMap<String, Double> baseValues;
    /**
     * Die Liste der Multiplikatoren
     */
    private HashMap<String, Double> multiplicators;

    public PropertyList() {
        baseValues = new HashMap<>();
        multiplicators = new HashMap<>();
    }

    /**
     * Gibt den Wert einer Eigenschaft zurück. Wenn die Eigenschaft nicht initialisiert wurde, wird 0 zurückgegeben.
     *
     * @param name der Name der gesuchten Eigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getProperty(String name) {
        if (baseValues.containsKey(name)) {
            return baseValues.get(name) * getMultiplicator(name);
        } else {
            return 0;
        }
    }

    /**
     * Setzt den Grundwert einer Eigenschaft. Falls der Multiplikator dieser Eigenschaft noch nicht gesetzt ist, wird er
     * auf 1.0 gesetzt.
     *
     * @param name der Name der Eigenschaft, deren Grundwert gesetzt werden soll
     * @param value der Wert, auf den der Grundwert der Eigenschaft gesetzt werden soll
     */
    final public void setBaseProperty(String name, double value) {
        baseValues.put(name, value);
    }

    /**
     * Gibt den Grundwert einer Eigenschaft zurück. Wenn die Eigenschaft nicht initialisiert wurde, wird 0
     * zurückgegeben.
     *
     * @param name der Name der gesuchten Grundeigenschaft
     * @return der Wert der Eigenschaft oder 0 wenn sie nicht gesetzt wurde.
     */
    final public double getBaseProperty(String name) {
        if (baseValues.containsKey(name)) {
            return baseValues.get(name);
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
    final public void incrementBaseProperty(String name, double value) {
        baseValues.put(name, getBaseProperty(name) + value);
    }

    /**
     * Dekrementiert den Wert einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, der dekrementiert werden soll
     * @param value der Wert, um den die Eigenschaft dekrementiert werden soll
     */
    final public void decrementBaseProperty(String name, double value) {
        baseValues.put(name, getBaseProperty(name) - value);
    }

    /**
     * Inkrementiert den Multiplikator einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, deren Multiplikator inkrementiert werden soll
     * @param value der Wert, um den der Multiplikator der Eigenschaft inkrementiert werden soll
     */
    final public void incrementMutliplicator(String name, double value) {
        multiplicators.put(name, getMultiplicator(name) + value);
    }

    /**
     * Dekrementiert den Multiplikator einer Eigenschaft
     *
     * @param name der Name der Eigenschaft, deren Multiplikator dekrementiert werden soll
     * @param value der Wert, um den der Multiplikator der Eigenschaft dekrementiert werden soll
     */
    final public void decrementMutliplicator(String name, double value) {
        multiplicators.put(name, getMultiplicator(name) - value);
    }

    /**
     * Gibt den Wert eines Multiplikators zurück. Wenn der Multiplikator nicht initialisiert wurde, wird 0
     * zurückgegeben.
     *
     * @param name der Name des gesuchten Multiplikators
     * @return der Wert des Multiplikators oder 1.0 wenn er nicht gesetzt wurde.
     */
    private double getMultiplicator(String name) {
        if (multiplicators.containsKey(name)) {
            return multiplicators.get(name);
        } else {
            return 1.0;
        }
    }

    /**
     * Setzt den Wert eines Multiplikators.
     *
     * @param name der Name der Eigenschaft, deren Multiplikator gesetzt werden soll
     * @param value der Wert, auf den der Multiplikator der Eigenschaft gesetzt werden soll
     */
    final public void setMutliplicator(String name, double value) {
        multiplicators.put(name, value);
    }

    /**
     * Addiert alle Grundwete eines anderen Properties-Objekts zu den Grundewerten dieser Properties hinzu. Addiert die
     * Multiplikatoren des anderen Properties-Objekts zu den Multiplikatoren dieser Properties hinzu.
     *
     * @param otherProperties die Properties die subtrahiert werden
     */
    public void addProperties(PropertyList otherProperties) {
        // Grundwerte:
        Iterator<Entry<String, Double>> iter = otherProperties.getBaseValueIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            incrementBaseProperty(next.getKey(), next.getValue());
        }
        // Multiplikatoren:
        iter = otherProperties.getMulitplicatorIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            incrementMutliplicator(next.getKey(), next.getValue());
        }
    }

    /**
     * Subtrahiert alle Grundwete eines anderen Properties-Objekts von den Grundewerten dieser Properties. Subtrahiert
     * die Multiplikatoren des anderen Properties-Objekts von den Multiplikatoren dieser Properties.
     *
     * @param otherProperties die Properties, die subtrahiert werden
     */
    public void removeProperties(PropertyList otherProperties) {
        // Grundwerte:
        Iterator<Entry<String, Double>> iter = otherProperties.getBaseValueIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            decrementBaseProperty(next.getKey(), next.getValue());
        }
        // Multiplikatoren:
        iter = otherProperties.getMulitplicatorIterator();
        while (iter.hasNext()) {
            Entry<String, Double> next = iter.next();
            decrementMutliplicator(next.getKey(), next.getValue());
        }
    }

    /**
     * Gibt einen Iterator über alle Grundwerte dieser Properties zurück
     *
     * @return ein Iterator über alle Grundwerte
     */
    private Iterator<Entry<String, Double>> getBaseValueIterator() {
        return baseValues.entrySet().iterator();
    }

    /**
     * Gibt einen Iterator über alle Multiplikatoren dieser Properties zurück
     *
     * @return ein Iterator über alle Mutliplikatoren
     */
    private Iterator<Entry<String, Double>> getMulitplicatorIterator() {
        return multiplicators.entrySet().iterator();
    }

    /**
     * Gibt eine Liste mit Werten, die in diesen Properties gesetzt sind, zurück.
     *
     * @return eine Liste mit Werten, die in diesen Properties gesetzt sind
     */
    @Override
    public String toString() {
        String text = "";
        Iterator<Entry<String, Double>> iter = baseValues.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Double> entry = iter.next();
            text += entry.getKey();
            text += " = ";
            text += entry.getValue();
            text += " * ";
            text += multiplicators.get(entry.getKey());
            text += "\n";
        }
        return text;
    }
}
