/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data;

/**
 * Ein Geschoss
 * @author J.K.
 */
public class Bullet {
    /*
     * Tick, in dem die Bullet erstellt wurde
     */

    private int spawntick;
    /*
     * Ort, an dem die Bullet erstellt wurde
     */
    private double spawnposx;
    private double spawnposy;
    /*
     * Richtung der Bullet
     */
    private double direction;
    /*
     * Geschwindigkeit
     */
    private float speed;
    /*
     * Schaden
     */
    private int damage;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;
    /*
     * netID.
     */
    private int netID;

    public Bullet(int spawntick, double spawnposx, double spawnposy, double direction, float speed, int netID) {
        this.spawntick = spawntick;
        this.spawnposx = spawnposx;
        this.spawnposy = spawnposy;
        this.direction = direction;
        this.speed = speed;
        this.deletetick = spawntick + 600; // Nach 10 Sekunden löschen
        this.netID = netID;
        this.damage = 3;
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
     * @return the spawnposition x
     */
    public double getSpawnposX() {
        return spawnposx;
    }

    /**
     * @return the spawnposition y
     */
    public double getSpawnposY() {
        return spawnposy;
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

    /**
     * @return the netID
     */
    public int getNetID() {
        return netID;
    }

    /**
     * @return the damage
     */
    public int getDamage() {
        return damage;
    }
}
