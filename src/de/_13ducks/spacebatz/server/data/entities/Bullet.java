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
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.util.Bits;
import java.util.ArrayList;

/**
 * Ein Geschoss. Geschosse sind Entities, die sich in eine Richtung bewegen bis sie mit etwas kollidieren oder ihre lifetime abgelaufen ist
 *
 * @author J.K.
 */
public class Bullet extends Entity {

    /**
     * Der Besitzer des Bullets, i.d.R. der Char der es erzeugt hat.
     */
    private Entity owner;
    /*
     * ID des BulletTypes
     */
    private final int bulletpic;
    /*
     * Tick, zu dem die Bullet gelöscht wird
     */
    private int deletetick;
    /**
     * Die Effekte, die dieses Geschoss hat.
     */
    private ArrayList<Effect> effects;
    /**
     * Gibt an, ob das Bullet Gegner trifft.
     */
    public boolean hitEnemies = true;

    /**
     * Erzeugt ein neues Bullet
     *
     * @param lifetime die Zahl der Ticks, nach der das Bullet gelöscht wird
     * @param spawnposx X-Koordinate des Anfangspunktes der Bewegung
     * @param spawnposy Y-Koordinate des Anfangspunktes der Bewegung
     * @param angle der Winkel, in dem das Bullet fliegt
     * @param speed die Geschwindigkeit des Bullets
     * @param pictureID die ID des Bildes des Bullets
     * @param netID die netID des Bullets
     * @param owner der Besitzer, i.d.R. der Char der das Bullet erzeugt hat
     */
    public Bullet(int lifetime, double spawnposx, double spawnposy, double angle, double speed, int pictureID, int netID, Entity owner) {
        super(spawnposx, spawnposy, netID, (byte) 4);
        this.bulletpic = pictureID;
        setVector(Math.cos(angle), Math.sin(angle));
        setSpeed(speed);
        this.owner = owner;
        this.deletetick = Server.game.getTick() + lifetime;
        effects = new ArrayList<>();
        this.setSize(CompileTimeParameters.BULLETSIZE);
    }

    public void hitChar(Entity hitChar) {
        if (hitChar instanceof Char) {
            Char target = (Char) hitChar;
            // Effekte an den getroffenen Char geben:
            for (Effect effect : effects) {
                effect.applyToChar(target);
            }
            //c.decreaseHitpoints();
            // AoE-Effekte auslösen:
            for (Effect effect : effects) {
                effect.applyToPosition(getX(), getY(), target);
            }

            // Das Bullet entfernen:
            Server.game.getEntityManager().removeEntity(netID);
        }
    }

    private void hitGround(double x, double y) {
        for (Effect effect : effects) {
            effect.applyToPosition(getX(), getY(), null);
        }
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
    public Entity getOwner() {
        return owner;
    }

    /**
     * Fügt dem Bullet einen neuen Effekt hinzu.
     *
     * @param effect der neue Effekt
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 8;
    }

    @Override
    public void netPack(byte[] pack, int offset) {
        super.netPack(pack, offset);
        Bits.putInt(pack, super.byteArraySize() + offset, bulletpic);
        Bits.putInt(pack, super.byteArraySize() + offset + 4, owner.netID);
    }

    @Override
    public void onCollision(Entity other) {
        if (other instanceof Enemy && !hitEnemies) {
            return;
        }
        super.onCollision(other);
        hitChar(other);
    }

    @Override
    public void onWallCollision(int[] collidingBlock) {
        super.onWallCollision(collidingBlock);
        if (Server.game.getLevel().isBlockDestroyable(collidingBlock[0], collidingBlock[1])) {
            Server.game.getLevel().destroyBlock(collidingBlock[0], collidingBlock[1]);
        }
        // Flächenschaden machen
        hitGround(getX(), getY());
        // Immer löschen, wenn Wand getroffen
        Server.game.getEntityManager().removeEntity(netID);
    }
}
