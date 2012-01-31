package de._13ducks.spacebatz;

/**
 *
 * @author Jojo
 */
public class ItemAttributeWeapon extends ItemAttribute implements java.io.Serializable {
    private float damage;
    private float attackspeed;
    private float range;
    
    public ItemAttributeWeapon(String name, int quality, float damage, float attackspeed, float range) {
        this.name = name;
        this.quality = quality;
        this.damage = damage;
        this.attackspeed = attackspeed;
        this.range = range;
        this.attributeclass = 1;
    }

    /**
     * @return the damage
     */
    public float getDamage() {
        return damage;
    }

    /**
     * @return the attackspeed
     */
    public float getAttackspeed() {
        return attackspeed;
    }

    /**
     * @return the range
     */
    public float getRange() {
        return range;
    }
}
