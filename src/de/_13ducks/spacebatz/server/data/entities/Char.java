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

import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.entities.move.Mover;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.PropertyList;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CHAR_INVISIBILITY;
import de._13ducks.spacebatz.util.Bits;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Charakter. Hat Eigenschaften (HP, Rüstung, ..) und kann von Effekten beeinflusst werden.
 *
 * @author michael
 */
public abstract class Char extends Entity {

    /**
     * Die Eigenschaften des Chars (Hitpoints, Rüstung etc).
     */
    protected PropertyList properties;
    /**
     * Der Tick, ab dem der Char wieder angreifen darf
     */
    public int attackCooldownTick;
    /**
     * Liste aller Effekte, die der EffectCarrier gerade hat
     */
    private ArrayList<Effect> effects;
    /**
     * Ist dieser Char gerade unsichtbar?
     */
    private boolean invisible;
    /**
     * Das Team dieses Chars
     */
    public Team team;
    /**
     * Liste der Chars, die diesen Char verfolgen.
     */
    public ArrayList<Char> hunters;
    /**
     * Übertrag für HP-Regeneration
     */
    private double hpRegCarryover;
    /**
     * Gibt an ob der Char tot ist.
     */
    public boolean dead = false;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x X-Koordinatedes Chars
     * @param y Y-Koordinatedes Chars
     * @param netID die netID des Chars
     * @param entityTypeID die typeID des Chars
     */
    public Char(int netID, byte entityTypeID, Mover mover, Team team) {
        super(netID, entityTypeID, mover);
        this.team = team;
        properties = new PropertyList();
        hunters = new ArrayList<>();
        properties.setMaxHitpoints(CompileTimeParameters.CHARHEALTH);
        properties.setHitpoints(CompileTimeParameters.CHARHEALTH);
        properties.setSightrange(10.0);
        effects = new ArrayList<>();
    }

    /**
     * Addiert alle Eigenschaften der angegebenen Properties zu den Properties des Chars.
     *
     * @param otherProperties die Properties, die addiert werden sollen
     */
    final public void addProperties(PropertyList otherProperties) {
        properties.addProperties(otherProperties);
        setSpeed(CompileTimeParameters.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Speed muss manuel gesetzt werden
    }

    /**
     * Zieht alle Eigenschaften der angegebenen Properties von den Eigenschaften dieses Chars ab.
     *
     * @param otherProperties die Properties, die subtrahiert werden sollen
     */
    final public void removeProperties(PropertyList otherProperties) {
        properties.removeProperties(otherProperties);
        setSpeed(CompileTimeParameters.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Speed muss manuel gesetzt werden
    }

    /**
     * Gibt die Properties dieses Chars zurück.
     *
     * @return die Propertie dieses Chars
     */
    public PropertyList getProperties() {
        return properties;
    }

    /**
     * Fügt einen neuen Effekt hinzu.
     *
     * @param newEffect der neue Effekt
     */
    public final void addTemporaryEffect(Effect newEffect) {
        effects.add(newEffect);
    }

    /**
     * Berechnet alle Effekte und entfernt abgelaufene Effekte.
     */
    @Override
    public void tick(int gametick) {
        super.tick(gametick);
        Iterator<Effect> iter = effects.iterator();
        while (iter.hasNext()) {
            Effect effect = iter.next();
            if (!effect.tick()) {
                effect.remove();
                iter.remove();
            }
        }
        // Chars können HP regenerieren, einmal pro Sekunde aufrufen
        if (properties.getHitpointRegeneration() != 0 && gametick % (1000 / CompileTimeParameters.SERVER_TICKRATE) == 0) {
            double bla = properties.getHitpointRegeneration() + hpRegCarryover;
            properties.setHitpoints(properties.getHitpoints() + (int) bla);
            if (properties.getHitpoints() > properties.getMaxHitpoints()) {
                properties.setHitpoints(properties.getMaxHitpoints());
            }
            hpRegCarryover = bla - (int) bla;
        }
    }

    /**
     * Char wird ein bestimmte Anzahl HP abgezogen
     *
     * @param damage
     */
    public void decreaseHitpoints(int damage) {
        double armor = getProperties().getArmor() * (1 + getProperties().getArmorMultiplicatorBonus());
        double damred = armor / (armor + 10);
        int newdamage = Math.max((int) (damage * (1 - damred)), 1);
        properties.setHitpoints(properties.getHitpoints() - newdamage);
        STC_CHAR_HIT.sendCharHit(netID, newdamage);
        if (properties.getHitpoints() <= 0) {
            dead = true;
        }
    }

    /**
     * @return the invisible
     */
    public boolean isInvisible() {
        return invisible;
    }

    /**
     * @param invisible the invisible to set
     */
    public void setInvisible(boolean invisible) {
        if (this.invisible != invisible) {
            this.invisible = invisible;
            STC_SET_CHAR_INVISIBILITY.broadcast(this);
        }
    }

    /**
     * Benachrichtigt alle Gegner die diesen Char verfolgen vom Tod.
     */
    public void onDeath() {
        for (Char hunter : hunters) {
            if (hunter instanceof Enemy) {
                ((Enemy) hunter).targetDied();
            }
        }
    }

    @Override
    public int byteArraySize() {
        return super.byteArraySize() + 2;
    }

    @Override
    public void netPack(byte[] b, int offset) {
        super.netPack(b, offset);
        Bits.putChar(b, super.byteArraySize() + offset, (isInvisible() ? '1' : '0'));
    }
}
