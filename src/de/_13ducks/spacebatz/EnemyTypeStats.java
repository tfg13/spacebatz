package de._13ducks.spacebatz;

/**
 *
 * @author Jj
 */
public class EnemyTypeStats {
    private int healthpoints;
    private int damage;
    private int picture;
    
    public EnemyTypeStats(int healthpoints, int damage, int picture) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
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
}
