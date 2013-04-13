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
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.WeaponAbility;
import de._13ducks.spacebatz.server.data.skilltree.MarsroverSkilltree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.PathNode;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TOGGLE_ALIVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TURRET_DIR_UPDATE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_SKILL_MAPPING;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SWITCH_WEAPON;
import de._13ducks.spacebatz.util.RingBuffer;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Der Spielercharakter. Verwaltet die Interaktion des Clients mit der Spielwelt.
 *
 * Achtung! Schreibrechte auf einige der Variablen und Methoden dieser Klasse unterliegen
 * möglicherweise den selben Einschränkungen wie die gesamte Klasse Entity.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;
    private WeaponAbility standardAttack = new FireBulletAbility(3, 1, 0.1, 9.0, 1, 0.2, 0.025, 0.0, 0.0, 0.0, true);
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
     * Die letzte empfangene Drehrichtung des Turrets.
     */
    private double turretDir;
    /**
     * In wieviel Ticks die Turret-Drehung das nächste mal versendet wird.
     */
    private int ticksUntilNextSend = 1;
    /**
     * Puffer der Positionen, auf denen der Spieler war.
     * Für AI benötigt.
     */
    private RingBuffer<PathNode> playerPath;
    /**
     * Leerer Observer für die Linearbewegungen.
     */
    private EntityLinearTargetObserver dummyObserver = new EntityLinearTargetObserver() {
        @Override
        public void targetReached() {
            moveDurationDelta = lastDuration;
        }

        @Override
        public void movementBlocked() {
        }

        @Override
        public void movementAborted() {
        }
    };
    /**
     * Speichert die Richtung einer gestarteten Client-Bewegung, die aber auf dem Server noch nicht gestartet wurde.
     */
    private Vector predictionWaitDirection;
    /**
     * Die letzte vom Client empfangene Bewegungsdauer.
     */
    private short lastDuration;
    /**
     * Dieses Delta wird immer in die Bewegungsdauer des Clients eingerechnet.
     * Der Wert von lastDuration wird hier eingefrohren, sollte die Entity auf dem Server wegen unterschiedlich langer
     * Signallaufzeiten kurz anhalten.
     */
    private short moveDurationDelta;

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
        playerPath = new RingBuffer<>(DefaultSettings.SERVER_AI_PLAYERPOSITION_BUFFERSIZE);
    }

    /**
     * Ein move-Request vom Client ist eingegangen.
     *
     * Teil der Bewegungs- und Predictionlogik.
     * Diese Methoden und alle dazugehörigen Submethoden und Variablen unterliegen deshalb
     * den gleichen Einschränkungen der Schreibrechte wie die gesamte Klasse Entity
     *
     * @param w W-Button gedrückt.
     * @param a A-Button gedrückt.
     * @param s S-Button gedrückt.
     * @param d D-Button gedrückt.
     * @param turretDir die Richtung des Turrets.
     * @param moveDuration seit wie vielen Ticks diese Bewegung schon läuft.
     */
    public void clientMove(boolean w, boolean a, boolean s, boolean d, float turretDir, short moveDuration) {
        this.turretDir = turretDir;
        lastDuration = moveDuration;
        moveDuration -= moveDurationDelta;
        // Bewegungsvektor:
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
        Vector direction = new Vector(x, y).normalize();
        // Gespeicherte, aber noch nicht gefahrene Bewegung darf nicht vergessen werden! (Das gilt sogar für tote Einheiten!)
        if (predictionWaitDirection != null && !predictionWaitDirection.equals(direction)) {
            // Manuell nachziehen:
            System.out.println("INFO: SPRED: Manual resttick");
            super.tick(Server.game.getTick());
        }
        // Jetzt ist die eins auf jeden Fall entweder ausgeglichen oder wir fahren länger, dann wird sie ohnehin ja erfasst.
        predictionWaitDirection = null;
        // Stoppen ist jetzt implizit, also nurnoch laufen berechnen
        if (!direction.equals(Vector.ZERO)) {
            if (!isMoving() && !dead) {
                // In jedem Fall eine neue Bewegung, falls länger als 1 Tick
                if (moveDuration > 1) {
                    setLinearTarget(getX() + (direction.x * getSpeed() * moveDuration), getY() + (direction.y * getSpeed() * moveDuration), dummyObserver);
                } else {
                    // Speichern, damit wir das nicht vergessen
                    predictionWaitDirection = direction;
                }
            } else {
                // Sonderfall stoppen, weil gestorben
                if (dead) {
                    stopMovement();
                    moveDurationDelta = 0;
                } else {
                    // Jetzt mit Prediction
                    // Laufen wir da schon hin?
                    if (Math.abs(getVecX() - direction.x) < .001 && Math.abs(getVecY() - direction.y) < .001) {
                        // Bewegung weiter erlauben:
                        setLinearTarget(getMoveStartX() + (direction.x * getSpeed() * moveDuration), getMoveStartY() + (direction.y * getSpeed() * moveDuration), dummyObserver);
                    } else {
                        // Richtung hat sich geändert, eine frühzeitige Zwangsberechnung durchführen, damit die Einheit noch bis zum target kommt.
                        super.tick(Server.game.getTick());
                        if (isMoving()) {
                            // Wir konnten nicht so weit fahren, wie der Client das gerne hätte.
                            System.out.println("WARNING: SPRED: Cannot guarantee smooth prediction!");
                        }
                        stopMovement();
                        moveDurationDelta = 0;
                        // Jetzt neue Bewegung starten, falls die Bewegung länger als 1 Tick ist:
                        if (moveDuration > 1) {
                            setLinearTarget(getX() + (direction.x * getSpeed() * moveDuration), getY() + (direction.y * getSpeed() * moveDuration), dummyObserver);
                        } else {
                            // Speichern, damit wir das nicht vergessen
                            predictionWaitDirection = direction;
                        }
                    }
                }
            }
        } else {
            moveDurationDelta = 0;
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
                double aspeed = standardAttack.getWeaponStats().getAttackspeed();
                if (getActiveWeapon() != null) {
                    aspeed = getActiveWeapon().getWeaponAbility().getWeaponStats().getAttackspeed();
                }

                if (getActiveWeapon() == null || getActiveWeapon().getWeaponAbility() == null) {
                    attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                    standardAttack.tryUseInAngle(this, angle);
                } else {
                    if (getActiveWeapon().getOverheat() + 1 <= getActiveWeapon().getWeaponAbility().getWeaponStats().getMaxoverheat() || getActiveWeapon().getWeaponAbility().getWeaponStats().getMaxoverheat() == 0) {
                        attackCooldownTick = Server.game.getTick() + (int) Math.ceil(1 / aspeed);
                        getActiveWeapon().increaseOverheat(1);
                        getActiveWeapon().getWeaponAbility().tryUseInAngle(this, angle);
                    }
                }
            }
        } else {
            if (Server.game.getTick() >= respawntick) {
                // respawnen
                properties.setHitpoints(CompileTimeParameters.CHARHEALTH);
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
        if (dead == false) {
            super.decreaseHitpoints(damage);
            if (properties.getHitpoints() <= 0) {
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
        if (gametick % DefaultSettings.SERVER_AI_PLAYERPOSITION_UPDATERATE == 0) {
            if (playerPath.get(0) != null) {
                double dx = Math.abs(getX() - playerPath.get(0).x);
                double dy = Math.abs(getY() - playerPath.get(0).y);
                if (dx != 0 || dy != 0) {
                    playerPath.insert(new PathNode(getX(), getY(), getVecX(), getVecY()));
                }
            } else {
                playerPath.insert(new PathNode(getX(), getY(), getVecX(), getVecY()));
            }

        }
    }

    @Override
    protected void directionChanged(double newVecX, double newVecY) {
        playerPath.insert(new PathNode(getX(), getY(), getVecX(), getVecY()));
    }

    /**
     * Gibt den Ringbuffer, der die Spielerpositionen speichert, zurück.
     */
    public RingBuffer<PathNode> getPlayerPath() {
        return playerPath;
    }
}
