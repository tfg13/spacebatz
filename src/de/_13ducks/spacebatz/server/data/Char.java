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
package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import java.util.HashMap;

/**
 * Ein bewegliches Objekt. (z.B. ein Spieler, Mob etc)
 *
 * @author michael
 */
public abstract class Char extends Entity {

    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints;
    /**
     * Die maximalen Lebenspunkte des Chars
     */
    protected int healthpointsmax;
    /**
     * Rüstung, verringert Schaden
     */
    protected int armor;
    /**
     * Der Schaden des Chars
     */
    protected int damage;
    /**
     * Die Sichtweite des Chars
     */
    protected int sightrange;
    /**
     * Die ID des Bildes für den Char
     */
    protected int pictureID = 0;
    /**
     * Wie lange nach Angriff gewartet werden muss, bis wieder angegriffen werden darf
     */
    protected int attackcooldown = 5;
    /**
     * Der Tick, ab dem wieder geschossen werden darf
     */
    protected int attackcooldowntick;
    /**
     * Reichweite für Angriffe
     */
    protected double range;
    /**
     * Die Größe des Chars (für Kollision)
     */
    private double size;
    /**
     * Die Liste aller Fähigkeiten, die der Char hat.
     * Chars können höchstens eine Fähigkeit pro FähigkeitenTyp haben!
     */
    private HashMap<Integer, Ability> abilities;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x X-Koordinatedes Chars
     * @param y Y-Koordinatedes Chars
     *
     */
    public Char(double x, double y, int netID, byte entityTypeID) {
        super(x, y, netID, entityTypeID);
        size = Settings.CHARSIZE;
        this.healthpoints = Settings.CHARHEALTH;
        this.healthpointsmax = Settings.CHARHEALTH;
        this.damage = 1;
        this.range = 10.0;
        this.sightrange = 10;
        this.attackcooldown = 60;
        abilities = new HashMap<>();
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars.
     *
     * @param ability die ID der Ability
     */
    public void useAbility(int ability) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).use();
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars auf eine Position.
     *
     * @param ability die ID der Ability
     * @param x X-Koordinate der Zielposition
     * @param y Y-Koordinate der Zielposition
     */
    public void useAbilityOnPosition(int ability, double x, double y) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useOnPosition(x, y);
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars in eine bestimmte Richtung.
     *
     * @param ability die ID der Ability
     * @param angle die Richtung in die die Fähigkeit benutzt werden soll
     */
    public void useAbilityInAngle(int ability, double angle) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useInAngle(range);
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars auf eine Zielentity.
     *
     * @param ability die ID der Ability
     * @param target das Ziel, auf das fie Fähigkeit benutzt werden soll
     */
    public void useAbilityOnTarget(int ability, Entity target) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useOnTarget(target);
        }
    }

    /**
     * Gibt dem Char eine neue Fähigkeit.
     *
     * @param id der Typ der neuen Fähigkeit
     * @param ability die neue Fähigkeit
     */
    public void addAbility(int id, Ability ability) {
        if (abilities.containsKey(id)) {
            throw new IllegalArgumentException("There is already a ability with that id!");
        } else {
            abilities.put(id, ability);
            ability.setOwner(this);
        }
    }

    /**
     * Entfernt die Fähigkeit eines Typs des Chars.
     *
     * @param id der Typ der Fähigkeit die entfernt werden soll
     */
    public void removeAbility(int id) {
        if (!abilities.containsKey(id)) {
            throw new IllegalArgumentException("Char has no such Ability!");
        } else {
            abilities.get(id).setOwner(null);
            abilities.remove(id);
        }
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
    }

    /**
     * Zieht Schadenspunkte von HP ab, returned true wenn Einheit stirbt
     *
     * @param e, Entity das Schaden zufügt
     * @return true, wenn Enemy stirbt, sonst false
     */
    public boolean decreaseHealthpoints(Entity e) {
        return false;
    }

    /**
     * Zieht Schadenspunkte von HP ab, returned true wenn Einheit stirbt
     *
     * @param e, Entity das Schaden zufügt
     * @return true, wenn Enemy stirbt, sonst false
     */
    public boolean decreaseHealthpoints(Entity e, double damagemodifier) {
        return false;
    }

    /**
     * @return the healthpoints
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the pictureID
     */
    public int getPictureID() {
        return pictureID;
    }

    /**
     * @return the sigthrange
     */
    public int getSightrange() {
        return sightrange;
    }

    /**
     * Gibt die Kollisionsgröße dieses Chars zurück
     *
     * @return die Kollisionsgröße
     */
    public double getSize() {
        return size;
    }

    /**
     * @return the range
     */
    public double getRange() {
        return range;
    }

    /**
     * @return the armor
     */
    public int getArmor() {
        return armor;
    }
}
