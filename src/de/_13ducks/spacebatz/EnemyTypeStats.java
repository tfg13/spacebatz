package de._13ducks.spacebatz;

/**
 *
 * @author Jj
 */
public class EnemyTypeStats implements java.io.Serializable{
    private int healthpoints;
    private int damage;
    private int picture;
    private float speed;
    private int sightrange;
    
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
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

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the sightrange
     */
    public int getSightrange() {
        return sightrange;
    }
}
