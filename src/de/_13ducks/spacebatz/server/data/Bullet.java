/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * ID des BulletTypes
     */
    public final int bulletpic;
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
