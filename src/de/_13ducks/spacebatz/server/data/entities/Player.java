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
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.SpellBook;
import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.WeaponAbility;
import de._13ducks.spacebatz.server.data.entities.move.DiscreteMover;
import de._13ducks.spacebatz.server.data.skilltree.MarsroverSkilltree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TOGGLE_ALIVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TURRET_DIR_UPDATE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_SKILL_MAPPING;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SWITCH_WEAPON;

/**
 * Der Spielercharakter. Verwaltet die Interaktion des Clients mit der Spielwelt.
 *
 * Wird vom Spieler direkt gesteuert und verwendet daher ein anderes Bewegungssystem wie der Rest.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

    /**
     * Bewegungssystem des Spielers.
     * Immer DiscreteMover, denn der Client muss die genaue Position vorhersagen können.
     */
    public final DiscreteMover move;
    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;
    private WeaponAbility standardAttack = new FireBulletAbility(3, 1, 0.1, 9.0, 1, 0.2, 0.025, 0.0, 0.0, 0.0);
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
    public boolean dead = false;
    /**
     * Ab wann der Spieler respawnwn kann
     */
    private int respawntick;
    /**
     * Die letzte empfangene Drehrichtung des Turrets.
     */
    private double turretDir;
    /**
     * In wieviel Ticks die Turret-Drehung das nächste mal versendet wird.
     */
    private int ticksUntilNextSend = 1;

    /**
     * Erzeugt einen neuen Player für den angegebenen Client. Dieser Player wird auch beim Client registriert. Es kann nur einen Player pro Client geben.
     *
     * @param x Startkoordinate X
     * @param y Startkoordinate Y
     * @param id netID, nicht mehr änderbar.
     * @param client der Client, dem dieser Player gehören soll.
     */
    public Player(double x, double y, int id, Client client, Team team) {
        super(id, (byte) 2, new DiscreteMover(x, y), team);
        this.move = (DiscreteMover) super.move;
        move.setEntity(this);
        client.setPlayer(this);
        this.client = client;
        skillTree = new MarsroverSkilltree();
        abilities = new SpellBook();
        properties.setHitpointRegeneration(CompileTimeParameters.PLAYERHEALTH_REGENERATION);
    }

    /**
     * Ein move-Request vom Client ist eingegangen.
     *
     * @param turretDir die Richtung des Turrets.
     * @param toX wo der Client sich laut seiner Prediction hinbewegt hat
     * @param toY wo der Client sich laut seiner Prediction hinbewegt hat
     */
    public void clientMove(float turretDir, float toX, float toY) {
        this.turretDir = turretDir;
        if (dead) { // Tote bewegen sich nicht
            move.step(move.getX(), move.getY());
        } else {
            move.step(toX, toY);
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
    public void playerShoot(double tx, double ty) {
        if (!dead) {
            if (Server.game.getTick() >= attackCooldownTick) {

                // Tick für nächsten erlaubten Angriff setzen (abhängig von Attackspeed)
                double aspeed = standardAttack.getWeaponStats().getAttackspeed();
                if (getActiveWeapon() != null) {
                    aspeed = getActiveWeapon().getWeaponAbility().getWeaponStats().getAttackspeed() * (1 + getActiveWeapon().getWeaponAbility().getWeaponStats().getAttackspeedMultiplicatorBonus());
                }

                if (getActiveWeapon() == null || getActiveWeapon().getWeaponAbility() == null) {
                    attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                    standardAttack.tryUseOnPosition(this, tx, ty);
                } else {
                    int maxOverheat = (int) (getActiveWeapon().getWeaponAbility().getWeaponStats().getMaxoverheat() * (1 + getActiveWeapon().getWeaponAbility().getWeaponStats().getMaxoverheatMultiplicatorBonus()));
                    if (getActiveWeapon().getOverheat() + 1 <= maxOverheat || getActiveWeapon().getWeaponAbility().getWeaponStats().getMaxoverheat() == 0) {
                        attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                        getActiveWeapon().increaseOverheat(1);
                        getActiveWeapon().getWeaponAbility().tryUseOnPosition(this, tx, ty);
                    }
                }
            }
        } else {
            if (Server.game.getTick() >= respawntick) {
                // respawnen
                properties.setHitpoints(properties.getMaxHitpoints());
                dead = false;
                STC_PLAYER_TOGGLE_ALIVE.sendPlayerToggleAlive(client.clientID, false);
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
                STC_ITEM_DEQUIP.sendItemDequip(slottype, selectedslot, getClient().clientID, (float) getSpeed());
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
        if (dead == false) {
            super.decreaseHitpoints(damage);
            if (properties.getHitpoints() <= 0) {
                super.onDeath();
                dead = true;
                respawntick = Server.game.getTick() + CompileTimeParameters.RESPAWNTIME;
                STC_PLAYER_TOGGLE_ALIVE.sendPlayerToggleAlive(client.clientID, true);
            }
        }
    }

    @Override
    public void tick(int gametick) {
        super.tick(gametick);
        if (--ticksUntilNextSend < 0) {
            ticksUntilNextSend = DefaultSettings.TURRET_DIR_UPDATE_INTERVAL;
            STC_PLAYER_TURRET_DIR_UPDATE.broadcastTurretDir(netID, (float) turretDir);
        }
    }
}
