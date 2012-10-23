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

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.SpellBook;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.WeaponAbility;
import de._13ducks.spacebatz.server.data.skilltree.MarsroverSkilltree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
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
    private WeaponAbility standardAttack = new FireBulletAbility(3, 10.0, 9.0, 1, 0.2, 0.025, 0.0);
    /**
     * Die Fähigkeiten mit Zuordnung.
     */
    private SpellBook abilities;
    /**
     * Der Skilltree, der bestimmt welche Fähigkeiten verfügbar sind.
     */
    private SkillTree skillTree;

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann
     * nur einen Player pro Client geben.
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
                if (Math.abs(vecX - x) > .001 || Math.abs(vecY - y) > .001) {
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
     * Lässt den Player seine "Shoot"-Fähigkeit auf ein Ziel einsetzen
     *
     * @param angle der Winkel in dem die Fähigkeit benutzt wird
     *
     */
    public void playerShoot(double angle) {
        if (getActiveWeapon() == null || getActiveWeapon().getWeaponAbility() == null) {
            standardAttack.useInAngle(this, angle);
        } else {
            getActiveWeapon().getWeaponAbility().useInAngle(this, angle);
            //useAbilityInAngle(0, angle);
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
                STC_ITEM_DEQUIP.sendItemDequip(slottype, selectedslot, (byte) 0, getClient().clientID);
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
        abilities.mapAbility(slot, skillTree.getSkillAbility(ability));
    }
    
    public void investSkillpoint(String ability) {
        skillTree.investPoint(ability);
        skillTree.sendSkillTreeUpdates(client);
    }
}
