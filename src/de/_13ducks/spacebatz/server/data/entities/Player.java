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

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.SpellBook;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.WeaponAbility;
import de._13ducks.spacebatz.server.data.skilltree.MarsroverSkilltree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TOGGLE_ALIVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_SKILL_MAPPING;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SWITCH_WEAPON;

/**
 * Der Spielercharakter. Verwaltet die Interaktion des Clients mit der Spielwelt.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;
    private WeaponAbility standardAttack = new FireBulletAbility(3, 0.1, 9.0, 1, 0.2, 0.025, 0.0, 0.0, 0.0);
    /**
     * Die Fähigkeiten mit Zuordnung.
     */
    private SpellBook abilities;
    /**
     * Der Skilltree, der bestimmt welche Fähigkeiten verfügbar sind.
     */
    private SkillTree skillTree;
    /**
     * Spieler ist tot und wartet auf Respawn
     */
    private boolean dead = false;
    /**
     * Ab wann der Spieler respawnwn kann
     */
    private int respawntick;

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann nur einen Player pro Client geben.
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     * @param client der Client, dem dieser Player gehören soll.
     */
    public Player(double x, double y, int id, Client client) {
        super(x, y, id, (byte) 2);
        client.setPlayer(this);
        this.client = client;
        skillTree = new MarsroverSkilltree();
        abilities = new SpellBook();
    }

    /**
     * Ein move-Request vom Client ist eingegangen. Teil der Netz-Infrastruktur, muss schnell verarbeitet werden
     *
     * @param w W-Button gedrückt.
     * @param a A-Button gedrückt.
     * @param s S-Button gedrückt.
     * @param d D-Button gedrückt.
     */
    public void clientMove(boolean w, boolean a, boolean s, boolean d) {
        double x = 0, y = 0;

        if (!dead) { // Tote bewegen sich nicht
            if (w) {
                y += 1;
            }
            if (a) {
                x += -1;
            }
            if (s) {
                y += -1;
            }
            if (d) {
                x += 1;
            }
        }
        // Sonderfall stoppen
        if (x == 0 && y == 0) {
            if (isMoving()) {
                stopMovement();
            }
        } else {
            // Bewegen wir uns zur Zeit schon (in diese Richtung)
            if (isMoving()) {
                double length = Math.sqrt((x * x) + (y * y));
                x /= length;
                y /= length;
                if (Math.abs(getVecX() - x) > .001 || Math.abs(getVecY() - y) > .001) {
                    this.setVector(x, y);
                }
            } else {
                setVector(x, y);
            }
        }
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * Lässt den Player seine "Shoot"-Fähigkeit auf ein Ziel einsetzen, falls Cooldown und Overheat das zulässt
     *
     * @param angle der Winkel in dem die Fähigkeit benutzt wird
     *
     */
    public void playerShoot(double angle) {
        if (!dead) {
            if (Server.game.getTick() >= attackCooldownTick) {

                // Tick für nächsten erlaubten Angriff setzen (abhängig von Attackspeed)
                double aspeed = standardAttack.getAttackspeed();
                if (getActiveWeapon() != null) {
                    aspeed = getActiveWeapon().getWeaponAbility().getAttackspeed();
                }

                if (getActiveWeapon() == null || getActiveWeapon().getWeaponAbility() == null) {
                    attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                    standardAttack.useInAngle(this, angle);
                } else {
                    if (getActiveWeapon().getOverheat() + 1 <= getActiveWeapon().getWeaponAbility().getMaxoverheat() || getActiveWeapon().getWeaponAbility().getMaxoverheat() == 0) {
                        attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                        getActiveWeapon().increaseOverheat(1);
                        getActiveWeapon().getWeaponAbility().useInAngle(this, angle);
                    }
                }
            }
        } else {
            if (Server.game.getTick() >= respawntick) {
                // respawnen
                properties.setHitpoints(Settings.CHARHEALTH);
                dead = false;
                STC_PLAYER_TOGGLE_ALIVE.sendPlayerToggleAlive(netID, false);
                attackCooldownTick = Server.game.getTick() + 30; // damit nicht sofort geschossen wird
            }
        }
    }

    /**
     * Item ablegen
     *
     * @param slottype Slotart (Waffe, Hut, ...)
     * @param selectedslot Nr. des Slots dieser Art
     */
    public void clientDequipItem(int slottype, byte selectedslot) {
        if (freeInventorySlot()) {
            // Item ins Inventar tun:
            if (dequipItemToInventar(slottype, selectedslot)) {
                STC_ITEM_DEQUIP.sendItemDequip(slottype, selectedslot, getClient().clientID);
            }
        }
    }

    /**
     * Wählt gerade aktive Waffe aus
     *
     * @param selectedslot aktiver Waffenslot (0 bis 2)
     */
    public void clientSelectWeapon(byte selectedslot) {
        if (setSelectedweapon(selectedslot)) {
            STC_SWITCH_WEAPON.sendWeaponswitch(getClient(), selectedslot);
        }
    }

    public void useAbility(byte ability, double x, double y) {
        abilities.useAbility(ability, x, y, this);
    }

    /**
     * Legt eine Fähigkeit auf den gewählten Slot
     *
     * @param slot
     * @param ability
     */
    public void mapAbility(byte slot, String ability) {
        if (skillTree.isSkillAvailable(ability)) {
            abilities.mapAbility(slot, skillTree.getSkillAbility(ability));
            STC_SET_SKILL_MAPPING.sendSetSkillMapping(getClient(), ability, slot);
        }
    }

    public void investSkillpoint(String ability) {
        skillTree.investPoint(ability);
        skillTree.sendSkillTreeUpdates(client);
    }

    @Override
    public void decreaseHitpoints(int damage) {
        super.decreaseHitpoints(damage);
        if (properties.getHitpoints() <= 0) {
            dead = true;
            respawntick = Server.game.getTick() + Settings.RESPAWNTIME;
            STC_PLAYER_TOGGLE_ALIVE.sendPlayerToggleAlive(netID, true);
        }
    }
}
