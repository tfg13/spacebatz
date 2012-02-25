package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;

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
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x
     * @param y
     * @param name
     */
    public Char(double x, double y, int netID, byte entityTypeID) {
        super(x, y, netID, entityTypeID);
        size = Settings.CHARSIZE;
        this.healthpoints = 10;
        this.healthpointsmax = 10;
        this.damage = 2;
        this.range = 1.0;
        this.sightrange = 10;
        this.attackcooldown = 10;
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
        if (e instanceof Enemy) {
            Enemy enemy = (Enemy) e;
            healthpoints -= enemy.getDamage();

            if (healthpoints <= 0) {
                Server.msgSender.sendCharHit(netID, enemy.netID, enemy.getDamage(), true);
                Server.game.netIDMap.remove(netID);
                Server.entityMap.removeEntity(this);
                return true;
            } else {
                Server.msgSender.sendCharHit(netID, enemy.netID, enemy.getDamage(), false);
                return false;
            }
        } else {
            return false;
        }
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
