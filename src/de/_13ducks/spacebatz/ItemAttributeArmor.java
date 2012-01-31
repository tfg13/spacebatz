package de._13ducks.spacebatz;

/**
 *
 * @author Jojo
 */
public class ItemAttributeArmor extends ItemAttribute implements java.io.Serializable {
    private float armor;
    private float movespeed;
    
    public ItemAttributeArmor(String name, int quality, float armor, float movespeed) {
        this.name = name;
        this.quality = quality;
        this.armor = armor;
        this.movespeed = movespeed;
        this.attributeclass = 2;
    }

    /**
     * @return the armor
     */
    public float getArmor() {
        return armor;
    }

    /**
     * @return the movespeed
     */
    public float getMovespeed() {
        return movespeed;
    }
}
