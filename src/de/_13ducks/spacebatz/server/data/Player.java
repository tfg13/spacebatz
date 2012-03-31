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

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;

/**
 * Der Spielercharakter
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Player extends ItemCarrier {

    /**
     * Der, Client, dem der Player gehört
     */
    private Client client;
    /*
     * Die möglichen Angriffe des Spielers (werden durch Waffen bestimmt)
     */
    private PlayerAttack[] attack = new PlayerAttack[3]; // # Waffenslots
    /*
     * Nummer des Angriffs, der zurzeit ausgewählt ist
     */
    private int selectedattack = 0;

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

        addAbility(Ability.SHOOT, new FireBulletAbility());
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
     * Lässt den Player seine "Shoot"-Fähigkeit einsetzen
     *
     * @param angle der Winkel in dem der Player schießen soll
     */
    public void playerShoot(float angle) {
        useAbilityInAngle(Ability.SHOOT, angle);
    }
}
