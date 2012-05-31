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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;

/**
 * Ein Geschoss, dass vom Client gerendert werden muss
 * 
 * @author Johannes
 */
public class Bullet extends Char {
    /*
     * Bild
     */
    public final int bulletpic;

    public Bullet(int netID, int bulletpic) {
        super(netID, new RenderObject(new Animation(bulletpic, 1, 1, 1)));
        this.bulletpic = bulletpic;
    }
}
