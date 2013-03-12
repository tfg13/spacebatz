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
package de._13ducks.spacebatz.client;

import de._13ducks.spacebatz.client.graphics.Animation;
import de._13ducks.spacebatz.client.graphics.RenderObject;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class Char {

    /**
     * Hier sind die Renderinformationen des Chars für die Grafik-Engine
     */
    protected RenderObject renderObject;
    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints;
    /**
     * Die maximalen Lebenspunkte des Chars
     */
    protected int healthpointsmax;
    /**
     * Die netID.
     */
    public final int netID;
    /**
     * Die Startposition der Bewegung.
     */
    private double x, y;
    /**
     * Die Richtung, in die dieser Char schaut.
     * Die übliche PI-Einheitskreis-Zählweise
     */
    private double dir = 0;
    /**
     * Die Richtung, in die dieser Char schauen soll.
     * Er dreht sich in jedem Tick etwas in diese Richtung.
     */
    private double target_dir = 0;
    /**
     * Die Richtung der Bewegung.
     */
    private double vX, vY;
    /**
     * Die Zieleinheit.
     */
    private Char target_Char;
    /**
     * Die Geschwindigkeit der Bewegung. (0 bei Stillstand)
     */
    private double speed;
    /**
     * Der Tick, zu dem die Bewegung begonnen hat.
     */
    private int startTick;
    /**
     * Der Tick, ab dem der Char wieder angreifen darf
     */
    public int attackCooldownTick;

    public Char(int netID, RenderObject renderObject) {
        this.netID = netID;
        if (renderObject != null) {
            this.renderObject = renderObject;
        } else {
            this.renderObject = new RenderObject(new Animation(0, 1, 1, 1, 1));
        }
        this.x = 0;
        this.y = 0;
    }

    /**
     * Liefert den aktuellen X-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle X-Position
     */
    public double getX() {
        if (target_Char == null) {
            // Normal
            return ((int) (16f * (x + ((GameClient.frozenGametick - startTick) * speed * vX)))) / 16f;
        } else {
            // Follow
            return x;
        }

    }

    /**
     * Liefert den aktuellen Y-Wert zurück Bewegungen sind hier schon eingerechnet.
     *
     * @return die aktuelle Y-Position
     */
    public double getY() {
        if (target_Char == null) {
            // Normal
            return ((int) (16f * (y + ((GameClient.frozenGametick - startTick) * speed * vY)))) / 16f;
        } else {
            // Follow
            return y;
        }

    }

    /**
     * @return the dir
     */
    public double getDir() {
        return dir;
    }

    /**
     * Wendet eine Bewegung auf diese Einheit an.
     *
     * @param sX Startposition, X
     * @param sY Startposition, Y
     * @param vX Richtung, X (normiert!)
     * @param vY Richtung, Y (normiert!)
     * @param startTick Startzeitpunkt in Logik-Ticks
     * @param speed Bewegungsgeschwindigkeit
     */
    public void applyMove(Movement m) {
        x = m.startX;
        y = m.startY;
        this.startTick = m.startTick;
        this.speed = m.speed;
        if (Float.isNaN(m.vecX)) {
            // Follow
            target_Char = GameClient.netIDMap.get(m.target_netID);
            // Nicht, normiert ist aber egal, weil sie nur für die Richtung verwendet werden.
            this.vX = target_Char.getX() - x;
            this.vY = target_Char.getY() - y;
        } else {
            // Normal
            target_Char = null;
            this.vX = m.vecX;
            this.vY = m.vecY;
        }
        // Nicht drehen beim Stehenbleiben
        if (startTick != -1) {
            this.target_dir = Math.atan2(vY, vX);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Char) {
            Char c = (Char) o;
            return c.netID == this.netID;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.netID;
        return hash;
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
    }

    /**
     * @param healthpoints the healthpoints to set
     */
    public void setHealthpoints(int healthpoints) {
        this.healthpoints = healthpoints;
    }

    /**
     * @return the healthpointsmax
     */
    public int getHealthpointsmax() {
        return healthpointsmax;
    }

    /**
     * @return the renderObject
     */
    public RenderObject getRenderObject() {
        return renderObject;
    }

    /**
     * Wird bei jedem gameTick aufgerufen.
     *
     * @param gameTick
     */
    public void tick(int gameTick) {
        if (target_Char != null) {
            // Einheit verschieben:
            Vector stepDirection = new Vector(target_Char.getX() - x, target_Char.getY() - y).normalize().multiply(speed);
            x += stepDirection.x;
            y += stepDirection.y;
            vX = stepDirection.x;
            vY = stepDirection.y;
            target_dir = Math.atan2(vY, vX);
        }
        // Etwas in Richtung target_dir drehen:
        if (Math.abs(target_dir - dir) <= DefaultSettings.CHAR_TURN_SPEED) {
            dir =  target_dir;
        } else {
            // Drehlogik zweiter Versuch
            double turnCurrent = dir;
            double turnTarget = target_dir;
            if (turnCurrent < 0) {
                turnCurrent += 2 * Math.PI;
            }
            if (turnTarget < 0) {
                turnTarget += 2 * Math.PI;
            }
            // Plus und Minus-Abstand suchen:
            double plusTurn = turnTarget - turnCurrent;
            if (plusTurn < 0) {
                plusTurn += 2 * Math.PI;
            }
            double minusTurn = turnCurrent - turnTarget;
            if (minusTurn < 0) {
                minusTurn += 2 * Math.PI;
            }
            // Jetzt in die kürzere Richtung drehen
            if (plusTurn <= minusTurn) {
                turnCurrent += DefaultSettings.CHAR_TURN_SPEED;
            } else {
                turnCurrent -= DefaultSettings.CHAR_TURN_SPEED;
            }
            // Wrap-Around
            if (turnCurrent > 2 * Math.PI) {
                turnCurrent -= 2 * Math.PI;
            } else if (turnCurrent < 0) {
                turnCurrent += 2 * Math.PI;
            }
            // Zurück schreiben
            if (turnCurrent <= Math.PI) {
                dir = turnCurrent;
            } else {
                dir = turnCurrent - 2 * Math.PI;
            }
        }
    }
}
