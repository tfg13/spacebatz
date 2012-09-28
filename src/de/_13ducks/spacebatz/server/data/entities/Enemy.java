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
import de._13ducks.spacebatz.server.gamelogic.AIManager;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein Gegner.
 *
 * @author J
 */
public class Enemy extends Char {

    /**
     * ID des Gegnertyps
     */
    private int enemytypeID = 0;
    /**
     * Der Char, den dieser Enemy gerade verfolgt
     */
    private Char myTarget;
    private int enemylevel;
    /**
     * Der KI-Typ des Gegners gibt an wie sich der Gegener verhält
     */
    private int AiType;

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
        AiType = AIManager.AITYPE_STANDARD; // Standard-KI
        this.enemytypeID = enemytypeID;
        EnemyTypeStats estats = Server.game.enemytypes.getEnemytypelist().get(enemytypeID);

        getProperties().setHitpoints(estats.getHealthpoints());
        //getProperties().setDamage(estats.getDamage());
        getProperties().setSightrange(estats.getSightrange());
        //getProperties().setPictureId(estats.getPicture());
        

        speed = estats.getSpeed();
        this.enemylevel = estats.getEnemylevel();

    }

    /**
     * Gibt den Char, den dieser Enemy gerade verfolgt, zurück.
     *
     * @return der Char der gerade verfolgt wird
     */
    public Char getMyTarget() {
        return myTarget;
    }

    /**
     * Setzt den Char, den dieser Enemy gerade verfolgt.
     *
     * @param der Char den dieser Enemy verfolgen soll
     */
    public void setMyTarget(Char myTarget) {
        this.myTarget = myTarget;
    }

    /**
     * @return the enemytypeid
     */
    public int getEnemytypeid() {
        return enemytypeID;
    }

    /**
     * Der Enemy will jemanden angreifen
     *
     * @param e das was angegriffen wird
     */
    public void attack(Char c) {
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
     * Gibt den KI-Typ dieses Gegners zurück
     *
     * @return der KI-Typ des GEgners
     */
    public int getAiType() {
        return AiType;
    }

    /**
     * @return the enemylevel
     */
    public int getEnemylevel() {
        return enemylevel;
    }
}
