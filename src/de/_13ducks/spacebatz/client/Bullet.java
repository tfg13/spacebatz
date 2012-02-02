package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.shared.BulletTypeStats;

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
    private float direction;
    /*
     * Geschwindigkeit
     */
    private float speed;
    /*
     * .
     */
    private int netID;
    /*
     * ID vom Bullettyp
     */
    private int bullettypeID;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;

    public Bullet(int spawntick, Position spawnposition, float direction, int bullettypeID, int netID) {
        BulletTypeStats stats = Client.bullettypes.getBullettypelist().get(bullettypeID);
        this.spawntick = spawntick;
        this.spawnposition = spawnposition;
        this.direction = direction;
        this.bullettypeID = bullettypeID;
        this.netID = netID;
        this.speed = stats.getSpeed();
        this.deletetick = spawntick + stats.getLifetime(); // Nach 10 Sekunden löschen
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
    public float getDirection() {
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

    /**
     * @return the netID
     */
    public int getNetID() {
        return netID;
    }

    /**
     * @return the bullettypeID
     */
    public int getBullettypeID() {
        return bullettypeID;
    }
}
