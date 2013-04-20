package de._13ducks.spacebatz.server.data.entities.move;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.shared.CompileTimeParameters;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.geo.Vector;

/**
 * Neues Bewegungssystem für von Menschen gesteuerte Players, deren Position sich auf
 * Client-Seite präzise vorhersagen lassen muss.
 *
 * Bewegt sich nicht auf einer interpolierten Bahn, sondern springt immer zum nächsten Punkt weiter.
 *
 * WIE ALLE KLASSEN IN DIESEM PAKET UNTERLIEGT AUCH DIESE EINER SCHREIBSPERRE!
 * NIEMAND AUßER MIR DARF DIESE KLASSE ÄNDERN!
 * ALLE ANDEREN ÄNDERUNGEN WERDEN ZURÜCKGESETZT!
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class DiscreteMover implements Mover {

    /**
     * Aktuelle Position dieses Players.
     * X-Richtung
     */
    private double x;
    /**
     * Aktuelle Position dieses Players.
     * Y-Richtung
     */
    private double y;
    /**
     * Der zuletzt gemachte Schritt.
     */
    private Vector lastStep = Vector.ZERO;
    /**
     * Aktuelle Geschwindigkeit dieses Players.
     */
    private double speed = CompileTimeParameters.BASE_MOVESPEED;
    /**
     * Die Entity, deren Position wir steuern.
     */
    private Entity entity;

    /**
     * Erstellt einen neuen DiscreteMover mit der gegebenen Startposition.
     *
     * @param x x-Startkoordinate
     * @param y y-Startkoordinate
     */
    public DiscreteMover(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Setzt die verwaltete Entity.
     * Kann nur ein einziges Mal aufgerufen werden.
     * Muss vor allen anderen Methoden aufgerufen werden.
     *
     * @param entity die master-Entity
     */
    public void setEntity(Entity entity) {
        if (this.entity != null) {
            throw new IllegalStateException("Cannot set master entity, this Mover already has one! (Master: " + entity + ")");
        }
        this.entity = entity;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public void setSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must not be smaller than or equal to zero!");
        }
        this.speed = speed;
    }

    @Override
    public void tick(int gametick) {
        // Do nothing - for now
    }

    /**
     * Läuft einen Schritt in die gegebenen Richtung.
     * Läuft nur, soweit es etwaige Hindernisse erlauben.
     * Läuft mit der derzeit eingestellten Geschwindigkeit.
     *
     * @param direction die Richtung, in die sich der Client bewegt
     */
    public void step(Vector direction) {
        Vector oldLastStep = lastStep;
        double lastX = x;
        double lastY = y;
        // Laufen:
        direction = direction.normalize().multiply(speed);
        x += direction.x;
        y += direction.y;
        double predictedX = x;
        double predictedY = y;
        int[] collision = computeCollision(lastX, lastY, x, y);
        if (x != predictedX || y != predictedY) {
            // Kollision!
            entity.onWallCollision(collision);
        }
        lastStep = new Vector(x - lastX, y - lastY);
        if (!oldLastStep.equals(lastStep)) {
            // Änderung der Bewegung --> SYNC
            Server.sync.updateMovement(entity);
        }
    }

    /**
     * Berechnet, ob wir uns vom angegebenen Startpunkt gefahrlos zum angegebenen Zielpunkt bewegen können. Geht davon aus, das wir uns bereits bewegen - nimmt sofort Korrekturen an der aktuellen
     * Bewegung vor. Liefert den Block zurück, mit dem wir als nächstes kollidieren.
     *
     * @param fromX Startpunkt X (muss frei sein)
     * @param fromY Startpunkt Y (muss frei sein)
     * @param toX Zielpunkt X
     * @param toY Zielpunkt Y
     * @return falls Kollision: Erster Kollisionsblock, sonst undefiniert
     */
    private int[] computeCollision(double fromX, double fromY, double toX, double toY) {
        // Der Vektor der Bewegung:
        double deltaX = toX - fromX;
        double deltaY = toY - fromY;
        // Anfangs- und Ziel-X des Gebiets das gescannt wird
        int moveAreaStartX = (int) (Math.min(fromX, toX) - entity.getSize() / 2);
        int moveAreaEndX = (int) (Math.max(fromX, toX) + entity.getSize() / 2) + 1;
        // Anfangs- und Ziel-Y des Gebiets das gescannt wird
        int moveAreaStartY = (int) (Math.min(fromY, toY) - entity.getSize() / 2);
        int moveAreaEndY = (int) (Math.max(fromY, toY) + entity.getSize() / 2) + 1;


        // Gesucht ist der Block, mit dem wir als erstes kollidieren
        // der Faktor für die weiteste Position auf die wir ohne Kolision vorrücken können: start + d * vector
        double d;
        // das kleinste gefundene d
        double smallestD = Double.MAX_VALUE;
        // Variablen, die wir in jedem Schleifendurchlauf brauchen:
        double blockMidX, blockMidY, d1, d2;
        // Den Block, mit dem wir kollidieren zwischenspeichern
        int[] collisionBlock = new int[2];
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int searchX = moveAreaStartX; searchX < moveAreaEndX; searchX++) {
            for (int searchY = moveAreaStartY; searchY < moveAreaEndY; searchY++) {
                if (Server.game.getLevel().getCollisionMap()[searchX][searchY] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromX) / deltaX;

                    // das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    // Y-Distanz berechnen, zum schauen ob wir nicht am Block mit y-Abstand vorbeifahren:
                    double yDistance = Math.abs(blockMidY - (fromY + d * deltaY));

                    if (!Double.isNaN(yDistance) && 0 <= d && d <= 1 && yDistance < ((entity.getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                            collisionBlock[0] = searchX;
                            collisionBlock[1] = searchY;
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
                if (Server.game.getLevel().getCollisionMap()[searchX][searchY] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = searchX + 0.5;
                    blockMidY = searchY + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (CompileTimeParameters.DOUBLE_EQUALS_DIST + 0.5 + entity.getSize() / 2.0)) - fromY) / deltaY;
                    // Das kleinere d wählen:
                    d = Math.min(d1, d2);

                    if (Double.isInfinite(d) || Double.isNaN(d) || d < 0) {
                        d = 0;
                    }

                    double xDistance = Math.abs(blockMidX - (fromX + d * deltaX));

                    if (!Double.isNaN(xDistance) && 0 <= d && d <= 1 && xDistance < ((entity.getSize() / 2.0) + 0.5)) {
                        // Wenn das d gültig ist *und* wir Y-Überschneidung haben, würden wir mit dem Block kollidieren
                        // Also wenn die Kollision näher ist als die anderen speichern:
                        if (d < smallestD) {
                            smallestD = d;
                        }
                        // Näher als die von X?
                        if (d < globalsmallestD) {
                            globalsmallestD = d;
                            collisionBlock[0] = searchX;
                            collisionBlock[1] = searchY;
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
            x = sx;
        }
        if (!Double.isNaN(sy)) {
            y = sy;
        }

        return collisionBlock;
    }

    @Override
    public boolean positionUpdateRequired() {
        return (!lastStep.equals(Vector.ZERO));
    }

    @Override
    public Movement getMovement() {
        if (lastStep.equals(Vector.ZERO)) {
            // Steht
            return new Movement(x, y, 0, 0, -1, speed);
        } else {
            return new Movement(x - lastStep.x, y - lastStep.y, lastStep.normalize().x, lastStep.normalize().y, Server.game.getTick() - 1, speed);
        }
    }
}
