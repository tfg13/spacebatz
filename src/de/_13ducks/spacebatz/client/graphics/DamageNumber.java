package de._13ducks.spacebatz.client.graphics;

/**
 * Schadenszahl auf der Map, wenn ein Gegner getroffen wird
 * @author Jojo
 */
public class DamageNumber {

    /**
     * Der Schaden, der angezeigt wird
     */
    private int damage;
    /**
     * x-Position
     */
    private double x;
    /**
     * y-Position
     */
    private double y;
    /**
     * Die Zeit, zu der die DamageNumber erstellt wurde
     */
    private long spawntime;

    protected DamageNumber(int damage, double x, double y, long spawntime) {
        this.damage = damage;
        this.x = x;
        this.y = y;
        this.spawntime = spawntime;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @return the spawntime
     */
    public long getSpawntime() {
        return spawntime;
    }
}
