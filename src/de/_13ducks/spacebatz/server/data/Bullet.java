/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.util.Bits;

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
     * Explosionsradius;
     */
    private double explosionradius;
    /*
     * ID des BulletTypes
     */
    private final int bulletpic;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;

    public Bullet(int spawntick, double spawnposx, double spawnposy, double angle, double speed, int typeID, int netID, Char owner) {
        super(spawnposx, spawnposy, netID, (byte) 4);
        moveStartTick = spawntick;

        this.bulletpic = typeID;

        setVector(Math.cos(angle), Math.sin(angle));
        setSpeed(speed);

        int lifetime;

        this.owner = owner;
        if (owner instanceof Player) {
            Player player = (Player) owner;
            this.damage = player.getSelectedAttack().getDamage();
            this.explosionradius = player.getSelectedAttack().getExplosionradius();
            lifetime = (int) (player.getSelectedAttack().getRange() / speed);
        } else {
            this.damage = owner.getDamage();
            lifetime = (int) (owner.getRange() / speed);
        }

        this.deletetick = spawntick + lifetime;
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
     * @return the explosionradius
     */
    public double getExplosionradius() {
        return explosionradius;
    }

    /**
     * @return the client
     */
    public Char getOwner() {
        return owner;
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 4;
    }

    @Override
    public void netPack(byte[] pack, int offset) {
        super.netPack(pack, offset);
        Bits.putInt(pack, super.byteArraySize() + offset, bulletpic);
    }
}
