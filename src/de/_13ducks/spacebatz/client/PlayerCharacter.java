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

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;
import de._13ducks.spacebatz.shared.Item;

/**
 * Der eigene Spieler
 *
 * @author JK
 */
public class PlayerCharacter extends Char {

    /**
     * Die gerade ausgewählte Waffe
     */
    private int selectedattack;
    /**
     * Ob der Spieler gerade tot ist
     */
    private boolean dead;
    /**
     * Ab wann der Spieler respawnwn kann
     */
    private int respawntick;

    public PlayerCharacter(int netID) {
        super(netID, new RenderObject(new Animation(0, 2, 2, 1, 1)));
        selectedattack = 0;
        healthpoints = Settings.CHARHEALTH;
        healthpointsmax = Settings.CHARHEALTH;
    }

    /**
     * @return the selectedattack
     */
    public int getSelectedattack() {
        return selectedattack;
    }

    /**
     * @param selectedattack the selectedattack to set
     */
    public void setSelectedattack(int selectedattack) {
        this.selectedattack = selectedattack;
    }

    @Override
    public void tick(int gameTick) {
        for (int i = 0; i <= 2; i++) {
            Item weapon = GameClient.getEquippedItems().getEquipslots()[1][i];
           
            if (weapon != null) {
                if (i != selectedattack || GameClient.frozenGametick >= attackCooldownTick) {
                    weapon.increaseOverheat(-weapon.getWeaponAbility().getReduceoverheat());
                }
            }
        }

    }

    /**
     * @return the dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @param dead the dead to set
     */
    public void setDead(boolean dead) {
        this.dead = dead;
        respawntick = GameClient.frozenGametick + Settings.RESPAWNTIME;
    }

    /**
     * @return the respawntick
     */
    public int getRespawntick() {
        return respawntick;
    }
}
