/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared;

import java.util.HashMap;

/**
 * Items können Attribute besitzen, die jeweils einen oder mehrere Itemwerte verändern.
 * @author Jojo
 */
public class ItemAttribute implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Name des Attributs
     */
    private String name;
    /**
     * Werte, die durch dieses Attribut geändert werden
     */
    private HashMap<String, Double> stats;

    public ItemAttribute(String name, HashMap<String, Double> stats) {
        this.name = name;
        this.stats = stats;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the stats
     */
    public HashMap<String, Double> getStats() {
        return stats;
    }
}
