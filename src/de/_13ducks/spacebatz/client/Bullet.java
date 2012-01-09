package de._13ducks.spacebatz.client;

/**
 * Ein Geschoss, dass vom Client gerendert werden muss
 * 
 * @author Johannes
 */
public class Bullet {
    /*
     * Tick, in dem die Bullet erstellt wurde
     */
    private int spawntick;
    /*
     * Ort, an dem die Bullet erstellt wurde
     */
    private Position spawnposition;
    /*
     * Richtung der Bullet
     */
    private double direction;
    /*
     * Geschwindigkeit
     */
    private float speed;
     /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;
    
    public Bullet(int spawntick, Position spawnposition, double direction, float speed) {
        this.spawntick = spawntick;
        this.spawnposition = spawnposition;
        this.direction = direction;
        this.speed = speed;
        this.deletetick = spawntick + 600; // Nach 10 Sekunden löschen
    }

    /**
     * @return the spawntick
     */
    public int getSpawntick() {
        return spawntick;
    }

    /**
     * @param spawntick the spawntick to set
     */
    public void setSpawntick(int spawntick) {
        this.spawntick = spawntick;
    }

    /**
     * @return the spawnposition
     */
    public Position getSpawnposition() {
        return spawnposition;
    }

    /**
     * @return the direction
     */
    public double getDirection() {
        return direction;
    }

    /**
     * @return the speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * @return the deletetick
     */
    public int getDeletetick() {
        return deletetick;
    }
}
