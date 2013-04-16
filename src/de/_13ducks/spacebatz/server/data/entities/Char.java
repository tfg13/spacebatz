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

import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.PropertyList;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CHAR_INVISIBILITY;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Charakter. Hat Eigenschaften (HP, Rüstung, ..) und kann vin Effekten beeinflusst werden.
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
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x X-Koordinatedes Chars
     * @param y Y-Koordinatedes Chars
     * @param netID die netID des Chars
     * @param entityTypeID die typeID des Chars
     */
    public Char(double x, double y, int netID, byte entityTypeID) {
        super(x, y, netID, entityTypeID);
        properties = new PropertyList();

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
    }

    /**
     * Char wird ein bestimmte Anzahl HP abgezogen
     *
     * @param damage
     */
    public void decreaseHitpoints(int damage) {
        properties.setHitpoints(properties.getHitpoints() - damage);
        STC_CHAR_HIT.sendCharHit(netID, damage);
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
}
