/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.BulletTypeStats;
import de._13ducks.spacebatz.server.Server;
import java.util.Random;

/**
 * Ein Geschoss
 * @author J.K.
 */
public class Bullet {
    private Char owner;
    
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
     * ID des BulletTypes
     */
    private int typeID;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;
    /*
     * netID.
     */
    private int netID;

    public Bullet(int spawntick, double spawnposx, double spawnposy, double direction, int typeID, int netID, Char owner) {
        Random random = new Random();
        BulletTypeStats stats = Server.game.bullettypes.getBullettypelist().get(typeID);
        this.typeID = typeID;
        this.spawntick = spawntick;
        this.spawnposx = spawnposx;
        this.spawnposy = spawnposy;       
        this.typeID = typeID;
        this.netID = netID;
        this.owner = owner;
        this.direction = direction + random.nextGaussian() * stats.getSpread();
        this.deletetick = spawntick + stats.getLifetime();
        this.damage = stats.getDamage();
        this.speed = stats.getSpeed();
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

    /**
     * @return the client
     */
    public Char getOwner() {
        return owner;
    }

    /**
     * @return the typeID
     */
    public int getTypeID() {
        return typeID;
    }
}
