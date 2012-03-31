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
 * Ein Geschoss.
 * Geschosse sind Entities, die sich in eine Richtung bewegen bis sie mit etwas kollidieren oder ihre lifetime
 * abgelaufen ist
 *
 * @author J.K.
 */
public class Bullet extends Entity {

    /**
     * Der Besitzer des Bullets, i.d.R. der Char der es erzeugt hat.
     */
    private Char owner;
    /*
     * ID des BulletTypes
     */
    private final int bulletpic;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;

    /**
     * Erzeugt ein neues Bullet
     *
     * @param spawntick der Tick, zu dem das Bullet erzeugt wurde bzw. mit der Bewegung begann
     * @param lifetime die Zahl der Ticks, nach der das Bullet gelöscht wird
     * @param spawnposx X-Koordinate des Anfangspunktes der Bewegung
     * @param spawnposy Y-Koordinate des Anfangspunktes der Bewegung
     * @param angle der Winkel, in dem das Bullet fliegt
     * @param speed die Geschwindigkeit des Bullets
     * @param pictureID die ID des Bildes des Bullets
     * @param netID die netID des Bullets
     * @param owner der Besitzer, i.d.R. der Char der das Bullet erzeugt hat
     */
    public Bullet(int spawntick, int lifetime, double spawnposx, double spawnposy, double angle, double speed, int pictureID, int netID, Char owner) {
        super(spawnposx, spawnposy, netID, (byte) 4);
        moveStartTick = spawntick;
        this.bulletpic = pictureID;
        setVector(Math.cos(angle), Math.sin(angle));
        setSpeed(speed);
        this.owner = owner;
        this.deletetick = spawntick + lifetime;
    }

    /**
     * Gibt den Tick, zu dem das Bullet gelöscht wird, zurück
     *
     * @return the deletetick
     */
    public int getDeletetick() {
        return deletetick;
    }

    /**
     * Gibt den Besitzer des Bullets zurück
     *
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
