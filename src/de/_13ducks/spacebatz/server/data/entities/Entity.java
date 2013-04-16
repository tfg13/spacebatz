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

import de._13ducks.spacebatz.server.data.entities.move.Mover;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ganz simple Oberklasse für alle Objekte im Spiel, die eine veränderbare Position auf der Map haben.
 *
 * Die Position und Bewegungen werden vom Netzwerksystem automatisch synchronisiert.
 * Kann nicht alleine instanziiert werden, Unterklasen müssen eine konkrete Implementierung einer Bewegungs und Positionslogik (Mover)
 * verwenden.
 *
 * @author tobi
 */
public abstract class Entity {

    /**
     * Die netID der Entity.
     */
    public final int netID;
    /**
     * Typ des Entity. Fürs Netzwerksystem
     */
    public final byte entityTypeID;
    /**
     * Das Bewegungssystem dieser Entity.
     */
    protected final Mover move;
    /**
     * Die aktuelle Positon auf der EntityMap.
     */
    private int[] entityMapPos;
    /**
     * Die Größe der Entity für Kollisionsberechnung.
     */
    private double size;

    /**
     * Konstruktor, erstellt eine neue Entity
     *
     * @param x die X-Koordinate der Entity
     * @param y die Y-Koordinate der Entity
     * @param netID die netId der Entity
     */
    public Entity(int netID, byte entityTypeID, Mover mover) {
        this.entityTypeID = entityTypeID;
        entityMapPos = new int[2];
        this.netID = netID;
        size = CompileTimeParameters.CHARSIZE;
        this.move = mover;
    }

    /**
     * Liefert die aktuelle X-Position dieser Entity.
     * Kann wegen Interpolation vom aktuellen Gametick abhängen (muss aber nicht).
     *
     * @return X-Koordinate
     */
    public double getX() {
        return move.getX();
    }

    /**
     * Liefert die aktuelle Y-Position dieser Entity.
     * Kann wegen Interpolation vom aktuellen Gametick abhängen (muss aber nicht).
     *
     * @return Y-Koordinate
     */
    public double getY() {
        return move.getY();
    }

    /**
     * Liefert die Geschwindigkeit dieser Entity in Feldern pro Tick.
     *
     * @return Geschwindigkeit der Entity in Feldern pro Tick
     */
    public double getSpeed() {
        return move.getSpeed();
    }

    /**
     * Setzt die Geschwindigkeit dieser Entity.
     * Werte kleiner oder gleich 0 sind verboten.
     *
     * @param speed neue Geschwindigkeit
     */
    public void setSpeed(double speed) {
        move.setSpeed(speed);
    }

    /**
     * Wie groß die Byte-Representation dieses Entitys ist. Die Größe darf 32 auf keinen Fall überschreiten! Implementierungen von Entity müssen diese Methode überschreiben und super.byteArraySize() +
     * Eigenbedarf zurückgeben!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 9;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses Chars in das gegebene Array. Das Array muss mindestens byteArraySize() + offset groß sein. Unterklassen müssen diese
     * Methode überschreiben, falls sie irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes oder ähnlich stehen. Überschriebene Methoden müssen erst super.netPack() aufrufen, und dann
     * selber den Puffer ab super.byteArraySize() + offset befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = entityTypeID;
        Bits.putInt(b, offset + 1, netID);
        Bits.putFloat(b, offset + 5, (float) size);
    }

    /**
     * Die aktuelle Positon auf der EntityMap.
     *
     * @return the entityMapPos
     */
    public int[] getEntityMapPos() {
        return entityMapPos;
    }

    /**
     * Die aktuelle Positon auf der EntityMap.
     *
     * @param entityMapPos the entityMapPos to set
     */
    public void setEntityMapPos(int[] entityMapPos) {
        this.entityMapPos = entityMapPos;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    protected void setSize(double size) {
        this.size = size;
    }

    /**
     * Wird aufgerufen, wenn diese Entity sich in eine andere bewegt.
     *
     * @param other die Entity, in die diese sich hineinbewegt hat
     */
    public void onCollision(Entity other) {
    }

    /**
     * Wird aufgerufen, wenn die Entity mit einer wand kollidiert.
     */
    public void onWallCollision(int[] collisionBlock) {
    }

    /**
     * Wird für jede Entity ein mal pro Tick aufgerufen.
     * Unterklassen MÜSSEN die tick-Methode ihrer direkten Oberklasse aufrufen!
     *
     * @param gametick der zu verwendenen Gametick
     */
    public void tick(int gametick) {
        move.tick(gametick);
    }
}
