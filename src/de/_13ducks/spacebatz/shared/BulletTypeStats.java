package de._13ducks.spacebatz.shared;

/**
 *
 * @author blub
 */
public class BulletTypeStats implements java.io.Serializable {

    private int damage;
    private int picture;
    private int numberofhits;
    private float speed;
    private float spread;
    private int lifetime;
    
    public BulletTypeStats(int damage, int picture, int numberofhits, float speed, float spread, int lifetime) {
        this.damage = damage;
        this.picture = picture;
        this.numberofhits = numberofhits;
        this.speed = speed;
        this.spread = spread;
        this.lifetime = lifetime;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the picture
     */
    public int getPicture() {
        return picture;
    }

    /**
     * @return the numberofhits
     */
    public int getNumberofhits() {
        return numberofhits;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return the spread
     */
    public float getSpread() {
        return spread;
    }

    /**
     * @return the lifetime
     */
    public int getLifetime() {
        return lifetime;
    }
    
}
