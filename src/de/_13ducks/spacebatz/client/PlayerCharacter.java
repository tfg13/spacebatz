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
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Repräsentiert einen Spieler auf dem Client.
 * Hält eine Spieler-spezifische Daten wie angelegte Waffe und Turret-Richtung.
 *
 * Hauptsächlich aber für die Prediction der Spielfigur zuständig.
 * Speichert dafür eine zusätzliche PredictedPosition,
 * die er mit den Bewegungsanfragen an den Server und der bekannten Geschwindigkeit selber aktualisiert.
 *
 * Wenn diese Position nicht zu arg von der korrekten, synchronisierten abweicht, wird sie für jegliche Grafikausgabe verwendet.
 *
 * @author JK
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class PlayerCharacter extends Char {

    /**
     * Die gerade ausgewählte Waffe
     */
    private int selectedattack;
    /**
     * Ab wann der Spieler respawnwn kann
     */
    private int respawntick;
    /**
     * RenderObject für das drehbare Turret.
     */
    private RenderObject turretRenderObject;
    /**
     * Richtung, in die das Turret gerade schaut.
     */
    private double turretDir;
    /**
     * Prediction an (true) oder aus (false).
     */
    private boolean predictMovements = false;
    /**
     * Von Prediction verwendete Bewegungsgeschwindigkeit.
     */
    private double prediction_speed = 0.17;
    /**
     * Vom Client vorhergesagte Position.
     * Kopiert die richtige Server-Position, wenn die Einheit steht oder Probleme mit der Prediction auftreten.
     */
    private double predictedX, predictedY;
    /**
     * Vom Client vorhergesagte Bewegungsrichtung.
     * 0, solange sich die Einheit nicht bewegt.
     */
    private Vector predictedVector = Vector.ZERO;
    /**
     * Vom Client vorhergesagter Tick, bei dem die aktuelle Bewegung begonnen hat.
     * -1, solange keine Bewegung vorhergesagt wird.
     */
    private int predictedstartTick = -1;
    /**
     * Der vorhergesagte Tick, bei dem die letzte Bewegung begonnen hat.
     */
    private int lastPredictedStartTick = -1;
    /**
     * True, solange die vorhergesagte Position zuverlässig ist.
     * Das bedeutet, dass sie nicht zu stark von der offiziellen Serverposition abweicht.
     */
    private boolean predictionAccurate;

    public PlayerCharacter(int netID, float size) {
        super(netID, size, new RenderObject(new Animation(0, 4, 4, 1, 1)));
        turretRenderObject = new RenderObject(new Animation(4, 4, 4, 1, 1));
        selectedattack = 0;
        healthpoints = CompileTimeParameters.CHARHEALTH;
        healthpointsmax = CompileTimeParameters.CHARHEALTH;
    }

    /**
     * @return the selectedattack
     */
    public int getSelectedattack() {
        return selectedattack;
    }

    /**
     * @param selectedattack the selectedattack to set
     */
    public void setSelectedattack(int selectedattack) {
        this.selectedattack = selectedattack;
    }

    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        if (this.netID == GameClient.logicPlayer.getPlayer().netID)
        for (int i = 0; i <= 2; i++) {
            Item weapon = GameClient.getEquippedItems().getEquipslots()[1][i];

            if (weapon != null) {
                if (i != selectedattack || GameClient.frozenGametick >= attackCooldownTick) {
                    weapon.increaseOverheat(-weapon.getWeaponAbility().getWeaponStats().getReduceoverheat());
                }
            }
        }
        computePrediction(gameTick);
    }

    /**
     * @return the respawntick
     */
    public int getRespawntick() {
        return respawntick;
    }

    /**
     * Liefert das RO des drehbaren Turrets.
     *
     * @return das RO des drehbaren Turrets
     */
    public RenderObject getTurretRenderObject() {
        return turretRenderObject;
    }

    /**
     * Liefert die Richtung, in die das Turret gerade schaut.
     *
     * @return die Richtung, in die das Turret gerade schaut
     */
    public double getTurretDir() {
        return turretDir;
    }

    public void setTurretDir(double dir) {
        turretDir = dir;
    }

    /**
     * Setzt die Einheitengeschwindigkeit für die Prediction neu.
     * Nur relevant, wenn die Prediction aktiviert ist, hat erstmal nichts mit der normalen Einheitengeschwindigkeit zu tun.
     *
     * @param prediction_speed the Geschwindigkeit in Feldern pro Tick
     */
    public void setPrediction_speed(float prediction_speed) {
        this.prediction_speed = prediction_speed;
    }

    /**
     * Schaltet die Bewegungsvorhersage für diesen PlayerCharacter an oder aus.
     *
     * @param prediction true für an, false für aus
     */
    public void setPrediction(boolean prediction) {
        this.predictMovements = prediction;
    }

    /**
     * Muss jeden Tick aufgerufen werden, sonst funktioniert die Prediction nicht.
     * Es müssen die WASD-Tastendrücke codiert übergeben werden.
     * Die oberen 4 Bits des übergebenen Bytes entsprechen den Buttons in der Reihenfolge WASD.
     *
     * @param buttons WASD0000, bitweise codiert
     */
    public void predictMovement(byte buttons) {
        /*
         * Für die Prediction eine neue Bewegung in die gegebene Richtung sofort starten lassen.
         * Natürlich vorher laufende Bewegungen abbrechen.
         */
        if (predictMovements) {
            // Richtung erfassen:
            Vector newDir = new Vector(((buttons & 0x40) != 0 ? -1 : 0) + ((buttons & 0x10) != 0 ? 1 : 0), ((buttons & 0x80) != 0 ? 1 : 0) + ((buttons & 0x20) != 0 ? -1 : 0)).normalize();
            // Aktuelle Bewegung vergleichen
            if (predictedstartTick != -1) {
                // Richtung ändern?
                if (!predictedVector.equals(newDir)) {
                    // Stoppen
                    if (predictionAccurate) {
                        System.out.println("STOP CGOTO " + (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.x + " " + (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.y);
                        //predictedX += (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.x;
                        //predictedY += (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.y;
                    } else {
                        System.out.println("WARN: PREDICT: Resetting Client position due to prediction accuracy issues");
                        predictedX = super.getX();
                        predictedY = super.getY();
                    }
                    System.out.println("SendSCTS at " + GameClient.frozenGametick);
                    lastPredictedStartTick = predictedstartTick;
                    predictedstartTick = -1;
                    predictedVector = Vector.ZERO;
                }
                // Richtung stimmt, do nothing
                return;
            }

            // Neue Bewegung starten?
            if (!newDir.equals(Vector.ZERO)) {
                System.out.println("SendMCTS at " + GameClient.frozenGametick);
                predictedVector = newDir;
                predictedstartTick = GameClient.frozenGametick - 1;
            }
        }
    }

    /**
     * Berechnet die Bewegungs-Vorhersage.
     * Verschiebt die Einheit und führt eine Kollisionsberechnung durch.
     */
    private void computePrediction(int tick) {
        if (predictMovements) {
            if (predictedstartTick != -1) {
                System.out.println("CGOTO: " + (tick - predictedstartTick) * prediction_speed * predictedVector.x + " " + (tick - predictedstartTick) * prediction_speed * predictedVector.y + " at " + tick);
                predictedX += (tick - predictedstartTick) * prediction_speed * predictedVector.x;
                predictedY += (tick - predictedstartTick) * prediction_speed * predictedVector.y;
                predictedstartTick = tick;
            } else {
                // Wenn wir uns schon lange nicht mehr bewegt haben und auch die Serverposition sich nicht bewegt,
                // dann diese einfach still übernehmen, damit sich Fehler nicht über die Zeit aufsummieren.
                if (!super.isMoving() && (tick - lastPredictedStartTick) > GameClient.getNetwork2().getLerp()) {
                    // Fehler ausgleichen, Position übernehmen:
                    predictedX = super.getX();
                    predictedY = super.getY();
                }
            }
            // Schätzung noch im Rahmen?
            Vector diff = new Vector(super.getX(), super.getY()).add(new Vector(-predictedX, -predictedY));
            if (diff.length() > CompileTimeParameters.CLIENT_PREDICT_MAX_DELTA_PER_LERP * GameClient.getNetwork2().getLerp()) {
                predictionAccurate = false;
            } else {
                predictionAccurate = true;
            }
        }
    }

    @Override
    public double getX() {
        /*
         * Liefert vorhergesagte Werte, falls die Prediction an ist, und diese einigermaßen im Rahmen sind.
         */
        if (!predictMovements || !predictionAccurate) {
            return super.getX();
        }

        return predictedX + (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.x;
    }

    @Override
    public double getY() {
        /*
         * Liefert vorhergesagte Werte, falls die Prediction an ist, und diese einigermaßen im Rahmen sind.
         */
        if (!predictMovements || !predictionAccurate) {
            return super.getY();
        }

        return predictedY + (GameClient.frozenGametick - predictedstartTick) * prediction_speed * predictedVector.y;
    }
}
