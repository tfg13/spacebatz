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
package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps.
     */
    private int enemytypeID = 0;
    /**
     * Das Level dieses Gegners. Beeinflusst die möglichen Drops.
     */
    private int enemylevel;

    /**
     * Erzeugt einen neuen Gegner
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param netid netID, nicht mehr änderbar.
     * @param typeid typeID gibt Gegnertyp an
     */
    public Enemy(double x, double y, int netid, int enemytypeID) {
        super(x, y, netid, (byte) 3);
        this.enemytypeID = enemytypeID;
        EnemyTypeStats estats = Server.game.enemytypes.getEnemytypelist().get(enemytypeID);
        getProperties().setHitpoints(estats.getHealthpoints());
        getProperties().setSightrange(estats.getSightrange());
        speed = estats.getSpeed();
        this.enemylevel = estats.getEnemylevel();
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 4;
    }

    @Override
    public void netPack(byte[] b, int offset) {
        super.netPack(b, offset);
        Bits.putInt(b, super.byteArraySize() + offset, enemytypeID);
    }

    /**
     * @return the enemylevel
     */
    public int getEnemylevel() {
        return enemylevel;
    }

    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
    }

    @Override
    public void onCollision(Entity other) {
        super.onCollision(other);
    }
}
