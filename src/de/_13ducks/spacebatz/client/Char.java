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
import de._13ducks.spacebatz.shared.CompileTimeParameters;
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
     * Die Lebenspunkt-Regeneration pro Sekunde
     */
    protected double hitpointRegeneration;
    /**
     * Übertrag für HP-Regeneration
     */
    private double hpRegCarryover;
    /**
     * Die Rüstung des Chars
     */
    protected int armor;
    /**
     * Die netID.
     */
    public final int netID;
    /**
     * Die Startposition der Bewegung.
     */
    private double x, y;
    /**
     * Die Richtung, in die dieser Char schaut. Die übliche
     * PI-Einheitskreis-Zählweise
     */
    private double dir = 0;
    /**
     * Die Richtung, in die dieser Char schauen soll. Er dreht sich in jedem
     * Tick etwas in diese Richtung.
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
     * Die Größe dieses Chars.
     */
    private float size;
    /**
     * Der Tick, zu dem die Bewegung begonnen hat.
     */
    private int startTick;
    /**
     * Der Tick, ab dem der Char wieder angreifen darf
     */
    public int attackCooldownTick;
    /**
     * Ob dieser Char gerade unsichtbar ist.
     */
    private boolean invisible = false;
    /**
     * Gibt an, ob die Blickrichtung dieses Chars auf einen anderen Char fixiert
     * ist.
     */
    private boolean isFacingTarget;
    /**
     * Die netId des Chars, auf den die Blickrichtung dieses Chars fixiert ist.
     */
    private int facingTargetNetId;
    /**
     * Gibt an ob dieser Char automatisch in Bewegungsrichtung schaut.
     */
    public boolean lookInMovingDirection;

    public Char(int netID, float size, RenderObject renderObject) {
        this.netID = netID;
        this.size = size;
        if (renderObject != null) {
            this.renderObject = renderObject;
        } else {
            this.renderObject = new RenderObject(new Animation(0, 1, 1, 1, 1));
        }
        this.x = 0;
        this.y = 0;
    }

    /**
     * Liefert den aktuellen X-Wert zurück Bewegungen sind hier schon
     * eingerechnet.
     *
     * @return die aktuelle X-Position
     */
    public double getX() {
        return internalGetX();
    }

    /**
     * Liefert den aktuellen Y-Wert zurück Bewegungen sind hier schon
     * eingerechnet.
     *
     * @return die aktuelle Y-Position
     */
    public double getY() {
        return internalGetY();
    }

    /**
     * Interne Wegberechungsmethode, die nicht überschrieben werden kann, und
     * deshalb garantiert nicht mit der Prediction in Konflikt gerät.
     *
     * @return die aktuelle X-Position, Bewegungen eingerechnet
     */
    private double internalGetX() {
        return x + ((GameClient.frozenGametick - startTick) * speed * vX);
    }

    /**
     * Interne Wegberechungsmethode, die nicht überschrieben werden kann, und
     * deshalb garantiert nicht mit der Prediction in Konflikt gerät.
     *
     * @return die aktuelle Y-Position, Bewegungen eingerechnet
     */
    private double internalGetY() {
        return y + ((GameClient.frozenGametick - startTick) * speed * vY);
    }

    /**
     * Liefert im Prinzip das Selbe wie getX(), interpoliert aber den aktuellen Subtick auch noch mit rein.
     * Eine so hohe zeitliche Auflösung ist vor allem für die Grafik interessant, die damit noch flüssigere Animationen erreicht.
     *
     * @param subTick der zu verwendende SubTick im üblichen 0..1+ Format
     * @return die sehr präzise X-Position
     */
    public double getSubtickedX(double subTick) {
        return internalGetSubtickedX(subTick);
    }

    /**
     * Liefert im Prinzip das Selbe wie getY(), interpoliert aber den aktuellen Subtick auch noch mit rein.
     * Eine so hohe zeitliche Auflösung ist vor allem für die Grafik interessant, die damit noch flüssigere Animationen erreicht.
     *
     * @param subTick der zu verwendende SubTick im üblichen 0..1+ Format
     * @return die sehr präzise Y-Position
     */
    public double getSubtickedY(double subTick) {
        return internalGetSubtickedY(subTick);
    }

    /**
     * Interne Wegberechnungsmethode, die nicht überschrieben werden kann und deshalb garantiert nicht mit der Prediction in Konflikt gerät.
     */
    private double internalGetSubtickedX(double subTick) {
        return x + (((GameClient.frozenGametick - startTick) + subTick) * speed * vX);
    }

    /**
     * Interne Wegberechnungsmethode, die nicht überschrieben werden kann und deshalb garantiert nicht mit der Prediction in Konflikt gerät.
     */
    private double internalGetSubtickedY(double subTick) {
        return y + (((GameClient.frozenGametick - startTick) + subTick) * speed * vY);
    }

    /**
     * @return the dir
     */
    public double getDir() {
        return dir;
    }

    /**
     * Liefert true, wenn die Einheit sich gerade bewegt.
     *
     * @return true, wenn die Einheit sich gerade bewegt
     */
    public boolean isMoving() {
        return startTick != -1;
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
            this.vX = target_Char.internalGetX() - x;
            this.vY = target_Char.internalGetY() - y;
        } else {
            // Normal
            target_Char = null;
            this.vX = m.vecX;
            this.vY = m.vecY;
        }
        // Nicht drehen beim Stehenbleiben
        if (startTick != -1 && !isFacingTarget && lookInMovingDirection) {
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

    public void setHealthpointsmax(int healthpointsmax) {
        this.healthpointsmax = healthpointsmax;
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
        if (isFacingTarget) {
            // Blickrichtung an Ziel anpassen:
            Char target = GameClient.netIDMap.get(facingTargetNetId);
            if (target != null) {
                double dx = target.getX() - getX();
                double dy = target.getY() - getY();
                target_dir = Math.atan2(dy, dx);
            } else {
                target_dir = 0;
            }
        }
        if (target_Char != null) {
            // Einheit verschieben:
            Vector stepDirection = new Vector(target_Char.internalGetX() - x, target_Char.internalGetY() - y).normalize().multiply(speed);
            x += stepDirection.x;
            y += stepDirection.y;
            if (!isFacingTarget) {
                // Blickrichtung an Bewegung anpassen:
                vX = stepDirection.x;
                vY = stepDirection.y;
                target_dir = Math.atan2(vY, vX);
            }
            startTick = gameTick;
        }
        // Etwas in Richtung target_dir drehen:
        if (Math.abs(target_dir - dir) <= DefaultSettings.CHAR_TURN_SPEED) {
            dir = target_dir;
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

        // Chars können HP regenerieren, einmal pro Sekunde aufrufen
        if (getHitpointRegeneration() != 0 && gameTick % (1000 / CompileTimeParameters.SERVER_TICKRATE) == 0) {
            double bla = getHitpointRegeneration() + hpRegCarryover;
            healthpoints += (int) bla;
            if (healthpoints > healthpointsmax) {
                healthpoints = healthpointsmax;
            }
            hpRegCarryover = bla - (int) bla;
        }

    }

    /**
     * @return the size
     */
    public float getSize() {
        return size;
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
        this.invisible = invisible;
    }

    /**
     * @return the hitpointRegeneration
     */
    public double getHitpointRegeneration() {
        return hitpointRegeneration;
    }

    /**
     * @param hitpointRegeneration the hitpointRegeneration to set
     */
    public void setHitpointRegeneration(double hitpointRegeneration) {
        this.hitpointRegeneration = hitpointRegeneration;
    }

    /**
     * @return the armor
     */
    public int getArmor() {
        return armor;
    }

    /**
     * @param armor the armor to set
     */
    public void setArmor(int armor) {
        this.armor = armor;
    }

    /**
     * Fixiert die Blickrichtung dieses Chars auf einen anderen Char.
     *
     * @param targetNetId
     */
    public void setFacing(int targetNetId) {
        isFacingTarget = true;
        facingTargetNetId = targetNetId;
    }

    /**
     * Deaktiviert die Fixierung der Blickrichtung dieses Chars auf sein Ziel.
     */
    public void stopFacing() {
        isFacingTarget = false;
    }
}
