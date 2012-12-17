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
import de._13ducks.spacebatz.shared.Item;

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
    public final int ownerid;

    public Bullet(int netID, int bulletpic, int ownerid) {
        super(netID, new RenderObject(new Animation(bulletpic, 1, 1, 1, 1)));
        this.bulletpic = bulletpic;
        this.ownerid = ownerid;
        
        // Overheat der Waffe erhöhen:
        if (ownerid == GameClient.player.netID) {
            int weaponnumber = GameClient.player.getSelectedattack();
            Item item = GameClient.getEquippedItems().getEquipslots()[1][weaponnumber];
            if (item != null) {
                GameClient.getEquippedItems().getEquipslots()[1][weaponnumber].increaseOverheat(1);
                GameClient.player.attackCooldownTick = GameClient.frozenGametick + (int) Math.ceil(1 / item.getWeaponAbility().getWeaponStats().getAttackspeed());
            }
        }
    }
}