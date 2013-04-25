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
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

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

    public Bullet(int netID, float size, int bulletpic, int ownerid) {
        super(netID, size, new RenderObject(new Animation(bulletpic, 1, 1, 1, 1)));
        this.bulletpic = bulletpic;
        this.ownerid = ownerid;

        // Overheat der Waffe erhöhen:
//        if (ownerid == GameClient.player.netID) {
//            int weaponnumber = GameClient.player.getSelectedattack();
//            Item item = GameClient.getEquippedItems().getEquipslots()[1][weaponnumber];
//            if (item != null) {
//                GameClient.getEquippedItems().getEquipslots()[1][weaponnumber].increaseOverheat(1);
//                GameClient.player.attackCooldownTick = GameClient.frozenGametick + (int) Math.ceil(1 / item.getWeaponAbility().getWeaponStats().getAttackspeed());
//            }
//        }
    }

    @Override
    public void applyMove(Movement movement) {
        // Wenn Owner predicted wird, die Startposition verschieben und die Richtung korrigieren
        Char owner = GameClient.netIDMap.get(ownerid);
        if (owner instanceof PlayerCharacter) {
            PlayerCharacter player = (PlayerCharacter) owner;
            if (player.isPredicted()) {
                // Startposition verschieben
                Vector predictionDelta = player.getPredictionDelta();
                Vector newStart = predictionDelta.getInverted().add(new Vector(movement.startX, movement.startY));
                Vector direction = new Vector(movement.vecX - newStart.x, movement.vecY - newStart.y).normalize();
                super.applyMove(new Movement(newStart.x, newStart.y, direction.x, direction.y, movement.startTick, movement.speed));
                return;
            }
        }
        // Normalfall, keine Prediction
        // Aus gegebenen Ziel die Richtung bestimmen
        Vector direction = new Vector(movement.vecX - movement.startX, movement.vecY - movement.startY).normalize(); // bedeutet target - start für beide Vektoren
        super.applyMove(new Movement(movement.startX, movement.startY, direction.x, direction.y, movement.startTick, movement.speed));
    }
}