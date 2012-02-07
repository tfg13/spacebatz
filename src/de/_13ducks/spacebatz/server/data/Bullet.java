/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.BulletTypeStats;
import java.util.Random;

/**
 * Ein Geschoss
 *
 * @author J.K.
 */
public class Bullet extends Entity {

    private Char owner;
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

    public Bullet(int spawntick, double spawnposx, double spawnposy, double direction, int typeID, int netID, Char owner) {
        super(spawnposx, spawnposy, netID);
        moveStartTick = spawntick;

        Random random = new Random();
        BulletTypeStats stats = Server.game.bullettypes.getBullettypelist().get(typeID);
        this.typeID = typeID;

        double angle = direction + random.nextGaussian() * stats.getSpread();
        setVector(Math.cos(angle), Math.sin(angle));
        setSpeed(stats.getSpeed());


        this.owner = owner;

        this.deletetick = spawntick + stats.getLifetime();
        this.damage = stats.getDamage();
        
    }

    /**
     * @return the spawntick
     */
    public int getSpawntick() {
        return moveStartTick;
    }

    /**
     * @return the spawnposition x
     */
    public double getSpawnposX() {
        return getX();
    }

    /**
     * @return the spawnposition y
     */
    public double getSpawnposY() {
        return getY();
    }

    /**
     * @return the direction
     */
    public double getDirectionX() {
        return vecX;
    }

    /**
     * @return the direction
     */
    public double getDirectionY() {
        return vecY;
    }

    /**
     * @return the deletetick
     */
    public int getDeletetick() {
        return deletetick;
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
