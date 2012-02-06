/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.shared;

/**
 * Items können ein oder mehrere Attribute besitzen, die die Itemstats verändern.
 * @author Jojo
 */
public class ItemAttribute  implements java.io.Serializable {

    protected String name;
    protected int quality;
    /**
     * Was für eine Art Attribut es ist
     * 1 - Waffe
     * 2 - Armor
     */
    protected int attributeclass;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the quality
     */
    public int getQuality() {
        return quality;
    }

    /**
     * @return the attributeType
     */
    public int getAttributeType() {
        return attributeclass;
    }
}
