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
import de._13ducks.spacebatz.server.data.SpellBook;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.shared.PropertyList;
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
    private PropertyList properties;
    public int attackCooldownTick;
    private SpellBook abilities;
    /**
     * Liste aller Effekte, die der EffectCarrier gerade hat
     */
    private ArrayList<Effect> effects;

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
        abilities = new SpellBook();
        properties.setHitpoints(Settings.CHARHEALTH);
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
        setSpeed(Settings.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Spped muss manuel gesetzt werden
    }

    /**
     * Zieht alle Eigenschaften der angegebenen Properties von den Eigenschaften dieses Chars ab.
     *
     * @param otherProperties die Properties, die subtrahiert werden sollen
     */
    final public void removeProperties(PropertyList otherProperties) {
        properties.removeProperties(otherProperties);
        setSpeed(Settings.BASE_MOVESPEED * (properties.getMovespeedMultiplicatorBonus() + 1)); // Spped muss manuel gesetzt werden
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
     * @return the abilities
     */
    public SpellBook getAbilities() {
        return abilities;
    }
}
