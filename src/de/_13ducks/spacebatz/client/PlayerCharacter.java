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
     * Ob der Spieler sich gerade bewegen kann.
     */
    private boolean paralyzed = false;
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
    private double prediction_speed = CompileTimeParameters.BASE_MOVESPEED;
    /**
     * Vom Client vorhergesagte Position.
     * Kopiert die richtige Server-Position, wenn die Einheit steht oder Probleme mit der Prediction auftreten.
     */
    private double predictedX, predictedY;
    /**
     * Prediction-Drehungen funktionieren genau wie die normalen.
     */
    private double predictedDir, predictedTargetDir;
    /**
     * Der Tick, bei dem zuletzt eine Prediction stattgefunden hat.
     */
    private double lastPredictionTick;

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
        if (this.netID == GameClient.logicPlayer.getPlayer().netID) {
            for (int i = 0; i <= 2; i++) {
                Item weapon = GameClient.getEquippedItems().getEquipslots()[1][i];

                if (weapon != null) {
                    if (i != selectedattack || GameClient.frozenGametick >= attackCooldownTick) {
                        weapon.increaseOverheat(-weapon.getWeaponAbility().getWeaponStats().getReduceoverheat());
                    }
                }
            }
        }
        computePrediction(gameTick);
    }

    public int getRespawntick() {
        return respawntick;
    }

    public void setRespawntick(int respawntick) {
        this.respawntick = respawntick;
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
     * Findert heraus, ob die Bewegungen dieses Spielers predicted werden.
     *
     * @return true, wenn Prediction aktiv ist
     */
    public boolean isPredicted() {
        return predictMovements;
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
        if (predictMovements && !paralyzed) {
            // Richtung erfassen:
            Vector newDir = new Vector(((buttons & 0x40) != 0 ? -1 : 0) + ((buttons & 0x10) != 0 ? 1 : 0), ((buttons & 0x80) != 0 ? 1 : 0) + ((buttons & 0x20) != 0 ? -1 : 0)).normalize();
            // Sollen wir uns überhaupt bewegen?
            if (!newDir.equals(Vector.ZERO)) {
                newDir = newDir.multiply(prediction_speed);
                // Jetzt verschieben:
                double oldX = predictedX;
                double oldY = predictedY;
                predictedX += newDir.x;
                predictedY += newDir.y;
                lastPredictionTick = GameClient.frozenGametick;
                computeCollision(oldX, oldY, predictedX, predictedY);
                predictedTargetDir = Math.atan2(newDir.y, newDir.x);
            }
        }
    }

    /**
     * Berechnet die Bewegungs-Vorhersage.
     * Verschiebt die Einheit und führt eine Kollisionsberechnung durch.
     */
    private void computePrediction(int tick) {
        if (predictMovements && !paralyzed) {
            if (GameClient.frozenGametick - lastPredictionTick > GameClient.getNetwork2().getLerp() + 5) {
                // Positionen nachkorrigieren, damit sich keine Rundungsfehler über die Zeit aufsummieren:
                if (predictedX != super.getX() || predictedY != super.getY()) {
                    if (Math.abs(predictedX - super.getX()) > .001 || Math.abs(predictedY - super.getY()) > .001) {
                        System.out.println("WARNING: CPRED: Major prediction correction was required:  X " + (super.getX() - predictedX) + " Y " + (super.getY() - predictedY));
                    }
                    predictedX = super.getX();
                    predictedY = super.getY();
                }
            }
            // Schätzung noch im Rahmen?
            Vector diff = new Vector(super.getX(), super.getY()).add(new Vector(-predictedX, -predictedY));
            if (diff.length() > CompileTimeParameters.CLIENT_PREDICT_MAX_DELTA_PER_LERP * GameClient.getNetwork2().getLerp()) {
                predictedX = super.getX();
                predictedY = super.getY();
                System.out.println("WARN: CPRED: Major prediction failure: X " + (super.getX() - predictedX) + " Y " + (super.getY() - predictedY));
            }
            // Etwas in Richtung predictedTargetDir drehen:
            if (Math.abs(predictedTargetDir - predictedDir) <= DefaultSettings.CHAR_TURN_SPEED) {
                predictedDir = predictedTargetDir;
            } else {
                // Drehlogik zweiter Versuch
                double turnCurrent = predictedDir;
                double turnTarget = predictedTargetDir;
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
                    predictedDir = turnCurrent;
                } else {
                    predictedDir = turnCurrent - 2 * Math.PI;
                }
            }
        }
    }

    @Override
    public double getX() {
        // Liefert vorhergesagte Werte, falls die Prediction an ist.
        if (!predictMovements && !paralyzed) {
            return super.getX();
        }

        return predictedX;
    }

    @Override
    public double getY() {
        // Liefert vorhergesagte Werte, falls die Prediction an ist.
        if (!predictMovements && !paralyzed) {
            return super.getY();
        }

        return predictedY;
    }

    @Override
    public double getDir() {
        if (!predictMovements && !paralyzed) {
            return super.getDir();
        }
        return predictedDir;
    }

    /**
     * Berechnet, ob wir uns vom angegebenen Startpunkt gefahrlos zum angegebenen Zielpunkt bewegen können. Geht davon aus, das wir uns bereits bewegen - nimmt sofort Korrekturen an der aktuellen
     * Bewegung vor.
     *
     * @param fromX Startpunkt X (muss frei sein)
     * @param fromY Startpunkt Y (muss frei sein)
     * @param toX Zielpunkt X
     * @param toY Zielpunkt Y
     */
    private void computeCollision(double fromX, double fromY, double toX, double toY) {
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - getSize() / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + getSize() / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - getSize() / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + getSize() / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int searchX = moveAreaStartX; searchX < moveAreaEndX; searchX++) {
            for (int searchY = moveAreaStartY; searchY < moveAreaEndY; searchY++) {
                if (GameClient.currentLevel.getCollisionMap()[searchX][searchY] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                    }
                }
            }
        }
        double sx = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann ohne kollision:
            sx = fromX + smallestD * deltaX;
        }

        // Für die Y-Berechung die Werte zurücksetzten, für die Block-Berechung aber behalten!
        double globalsmallestD = smallestD;
        smallestD = Double.MAX_VALUE;
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int searchX = moveAreaStartX; searchX < moveAreaEndX; searchX++) {
            for (int searchY = moveAreaStartY; searchY < moveAreaEndY; searchY++) {
                if (GameClient.currentLevel.getCollisionMap()[searchX][searchY] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                        // Näher als die von X?
                        if (d < globalsmallestD) {
                            globalsmallestD = d;
                        }
                    }
                }
            }
        }
        double sy = Double.NaN;
        // Hier haben wir mit smallestD und xCollision alle relevanten infos
        if (smallestD < Double.MAX_VALUE) {
            // Die Koordinaten der Position die noch erreicht werden kann
            sy = fromY + smallestD * deltaY;
        }

        // Bewegung koorigieren?
        if (!Double.isNaN(sx)) {
            predictedX = sx;
        }
        if (!Double.isNaN(sy)) {
            predictedY = sy;
        }
    }

    public void setParalyzed(boolean paralyzed) {
        this.paralyzed = paralyzed;
    }

    /**
     * Liefert einen Vektor der den aktuellen Unterschied zwischen Serverposition und Prediction beschreibt.
     * Darf nicht aufgerufen werden, wenn die Einheit gar nicht predicted wird.
     * Der Vektor zeigt von der öffentlichen, vorhergesagten Position zur Serverposition.
     *
     * @return Delta-Vektor
     */
    public Vector getPredictionDelta() {
        if (!predictMovements) {
            throw new IllegalStateException("Cannot return prediction-delta, prediction is disabled!");
        }
        return new Vector(super.getX() - predictedX, super.getY() - predictedY);
    }
}
