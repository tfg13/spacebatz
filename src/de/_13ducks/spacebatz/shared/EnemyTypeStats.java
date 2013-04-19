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
package de._13ducks.spacebatz.shared;

/**
 * Beschreibt alle eigenschaften eines Gegnertyps.
 *
 * @author Jj
 */
public class EnemyTypeStats implements java.io.Serializable {

    private static long serialVersionUID = 1L;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @param aSerialVersionUID the serialVersionUID to set
     */
    public static void setSerialVersionUID(long aSerialVersionUID) {
        serialVersionUID = aSerialVersionUID;
    }
    public int healthpoints;
    public int damage;
    public int picture;
    public float speed;
    public int sightrange;
    public int enemylevel;
    public float color_red;
    public float color_green;
    public float color_blue;
    public float color_alpha;
    /**
     * Das Verhalten dieses Gegnertyps.
     */
    public BehaviourType behaviour;
    /**
     * Die Schieß-Fähigkeit dieses Gegnertyps.
     */
    public AbilityType shootAbility;

    /**
     * Liste der möglichen Gegner-Verhalten
     */
    public static enum BehaviourType {

        SHOOTER, SPECTATOR, KAMIKAZE, LURKER
    };

    /**
     * Liste der möglichen Gegnerfähigkeiten.
     */
    public static enum AbilityType {

        FIREBULLET, NONE, KAMIKAZE
    };

    /**
     * Initialisiert ein leeres EnemytypeStats-Objekt.
     */
    public EnemyTypeStats() {
        color_alpha = 1.0f;
        color_blue = 1.0f;
        color_green = 1.0f;
        color_red = 1.0f;
    }

    /**
     * Werte für einen Gegnertyp
     *
     * @param healthpoints Lebenspunkte
     * @param damage Schaden
     * @param picture Bildnummer in der Gegnertilemap
     * @param speed Geschwindigkeit
     * @param sightrange Distanz, ab der er auf en Spieler reagiert
     * @param enemylevel Gegnerlevel, soll gedroppte Items beeinflussen
     */
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
        this.enemylevel = enemylevel;
        this.color_red = 1f;
        this.color_blue = 1f;
        this.color_green = 1f;
        this.color_alpha = 1f;
    }

    /**
     * Werte für einen Gegnertyp mit Einfärben
     *
     * @param healthpoints Lebenspunkte
     * @param damage Schaden
     * @param picture Bildnummer in der Gegnertilemap
     * @param speed Geschwindigkeit
     * @param sightrange Distanz, ab der er auf en Spieler reagiert
     * @param enemylevel Gegnerlevel, soll gedroppte Items beeinflussen
     * @param color_red rote Farbe
     * @param color_blue blaue Farbe
     * @param color_green grüne Farbe
     * @param color_alpha Alpha-Farbwert
     */
    public EnemyTypeStats(int healthpoints, int damage, int picture, float speed, int sightrange, int enemylevel, float color_red, float color_green, float color_blue, float color_alpha) {
        this.healthpoints = healthpoints;
        this.damage = damage;
        this.picture = picture;
        this.speed = speed;
        this.sightrange = sightrange;
        this.enemylevel = enemylevel;
        this.color_red = color_red;
        this.color_blue = color_blue;
        this.color_green = color_green;
        this.color_alpha = color_alpha;
    }
}
