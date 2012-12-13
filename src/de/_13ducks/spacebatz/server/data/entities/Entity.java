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
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;

/**
 * Oberklasse für alle Objekte im Spiel Dazu gehören Chars, Mobs, Pflanzen, ... Enthält Position und Bewegungsinformationen
 *
 * Diese Klasse ist die Einzige, die die Position einer Entity verändern kann.
 *
 * Diese Klasse macht im Wesentlichen nur Bewegungs-Sachen.
 * Der Code wird teils von mehreren Stellen gleichzeitig aufgerufen, und muss
 * garantieren können, dass Entitys niemals in der Wand anhalten.
 * Deshalb:
 * DIESE KLASSE DARF VON NIEMANDEM VERÄNDERT WERDEN.
 * UND SEI DIE ÄNDERUNG AUCH NOCH SO KLEIN. ICH SETZE ALLES GNADENLOS ZURÜCK.
 * NUR ICH DARF DIESE KLASSE ÄNDERN.
 *
 * Das Bewegungssystem unterstützt 2 Laufmodi.
 * Der erste wird mit setVector gestartet und lässt die Entity einfach in eine Richtung laufen.
 * Die Entity hält nur an, falls ein Hindernis auftaucht, oder die Bewegung mit stopMovement angehalten, bzw. mit setVector geändert wird.
 * Der zweite Modus wird mit setLinearTarget aufgerufen und startet eine lineare Bewegung bis zum Ziel. Hier meldet sich die Entity beim
 * Listener zurück, wenn das Ziel erreicht wird, oder wenn ein Hindernis den Weg blockiert.
 * Aufrufen von setVector im zweiten Modus oder setLinearTarget im ersten bricht die jeweilige Bewegung ab.
 *
 * Mit einer Bewegung im zweiten Modus kann, im Gegensatz zum ersten, ein bestimmter Zielpunkt exakt erreicht werden.
 * Hier werden vom Bewegungssystem alle Verschiebungen wegen Tickdelay oder Fließkomma-Ungenauigkeiten rausgerechnet.
 *
 * @author tobi
 */
public class Entity {

    /**
     * Die netID der Entity.
     */
    public final int netID;
    /**
     * Tpy des Entity. Fürs Netzwerksystem
     */
    public final byte entityTypeID;
    /**
     * Die Position dieser Einheit in X-Richtung.
     */
    private double posX;
    /**
     * Die Position dieser Einheit in Y-Richtung.
     */
    private double posY;
    /**
     * Die Startposition der aktuellen Bewegung. Nur für Client.
     */
    private double moveStartX;
    /**
     * Die Startposition der aktuellen Bewegung. Nur für Client.
     */
    private double moveStartY;
    /**
     * Restlänge des aktuellen Streckenabschnitts.
     * -1, wenn kein Streckenfahren aktiv
     */
    private double remainingPathLength = -1;
    /**
     * Das Ziel des aktuellen Steckenabschnitts in X-Richtung.
     */
    private double targetX;
    /**
     * Das Ziel des aktuellen Streckenabschnitts in Y-Richtung.
     */
    private double targetY;
    /**
     * Der Observer der aktuellen target-Bewegung.
     * Muss immer gesetzt sein, wenn die Einheit in Modus 2 läuft.
     * Ansonsten nicht definiert.
     */
    private EntityLinearTargetObserver observer;
    /**
     * Die Geschwindigkeit der Bewegung
     */
    private double speed = .17;
    /**
     * Der Gametick in dem die Bewegung gestartet wurde (Muss für den Client gespeichert werden)
     */
    private int moveStartTick;
    /**
     * Die X-Koordinate des Bewegungsvektors
     */
    private double vecX;
    /**
     * Die Y-Koordinate des Bewegungsvektors
     */
    private double vecY;
    /**
     * True, wenn die Einheit sich gerade bewegt.
     */
    private boolean moving = false;
    /**
     * Die aktuelle Positon auf der EntityMap.
     */
    private int[] entityMapPos;
    /**
     * Die Größe der Entity für Kollisionsberechnung.
     */
    private double size;

    /**
     * Konstruktor, erstellt eine neue Entity
     *
     * @param x die X-Koordinate der Entity
     * @param y die Y-Koordinate der Entity
     * @param netID die netId der Entity
     */
    public Entity(double x, double y, int netID, byte entityTypeID) {
        this.entityTypeID = entityTypeID;
        this.posX = x;
        this.posY = y;
        entityMapPos = new int[2];
        this.netID = netID;
        size = Settings.CHARSIZE;
    }

    /**
     * Liefert true, wenn die Einheit sich gerade bewegt.
     *
     * @return true, wenn die Einheit sich gerade bewegt
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Setzt die Position dieser Einheit auf den angegebenen Wert. Wenn entweder x oder y (nicht beide!) NaN sind, wird die Bewegung nur in eine Richtung angehalten. Darf nicht aufgerufen werden, wenn
     * sich die Einheit gar nicht bewegt.
     *
     * @param x die X-Stop-Koordinate oder NaN
     * @param y die Y-Stop-Koordinate oder NaN
     */
    private void setStopXY(double x, double y) {
        boolean xCont = Double.isNaN(x);
        boolean yCont = Double.isNaN(y);
        if (xCont && yCont) {
            throw new IllegalArgumentException("Cannot setStop without stopping at all! (x=y=NaN)");
        }
        if (!moving) {
            throw new IllegalArgumentException("Cannot setStop, Entity is not moving at all!");
        }
        // Linearmodus abschalten, damit stopMovement() nicht (nochmal) den Observer benachrichtigt.
        boolean pathColBlocked = false;
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            pathColBlocked = true;
        }
        if (xCont) {
            posY = y;
            vecY = 0;
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            if (vecX == 0) {
                moving = false;
            }
        } else if (yCont) {
            posX = x;
            vecX = 0;
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            if (vecY == 0) {
                moving = false;
            }
        } else {
            stopMovement();
            posX = x;
            posY = y;
        }
        if (pathColBlocked) {
            observer.movementBlocked();
        }
    }

    /**
     * Stoppt die Einheit sofort. Wenn die Einheit sich gar nicht bewegt passiert nichts.
     */
    public void stopMovement() {
        moving = false;
        moveStartTick = -1;
        Server.sync.updateMovement(this);
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            observer.movementAborted();
        }
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Einheiten verschieben höchstens ein mal pro Tick ihre Position. (das ist normale Bewegung) Ansonsten ist diese Position konstant.
     *
     * @return Die X-Position dieses Chars
     */
    public double getX() {
        return posX;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Einheiten verschieben häcshtens ein mal pro Tick ihre Position. (das ist normale Bewegung) Ansonsten ist diese Position konstant.
     *
     * @return Die Y-Position dieses Chars
     */
    public double getY() {
        return posY;
    }

    /**
     * Setzt den Bewegungsvektor dieses Chars neu.
     * Die Einheit bewegt sich nach dem Aufruf in diese Richtung im Modus 1.
     * Der Vektor wird normalisiert, kann also die Geschwindigkeit nicht beeinflussen.
     * Das geht nur mit setSpeed. x und y dürfen nicht beide 0 sein!
     */
    public void setVector(double x, double y) {
        if (x == 0 && y == 0) {
            throw new IllegalArgumentException("Cannot set moveVector, x = y = 0 is not allowed!");
        }
        normalizeAndSetVector(x, y);
        moving = true;
        // Das ist eine neue Client-Bewegung
        moveStartTick = Server.game.getTick();
        moveStartX = posX;
        moveStartY = posY;
        Server.sync.updateMovement(this);
        if (remainingPathLength != -1) {
            remainingPathLength = -1;
            observer.movementAborted();
        }
    }

    /**
     * Liefert die Geschwindigkeit dieser Einheit zurück.
     *
     * @return die Geschwindigkeit dieser Einheit
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt.
     * Kann auch während einer Bewegung aufgerufen werden.
     *
     * @param speed die neue Geschwindigkeit > 0
     */
    public void setSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Cannot set speed: Must be greater than zero");
        }
        this.speed = speed;
        if (moving) {
            // Neue Client-Bewegung simulieren
            moveStartTick = Server.game.getTick();
            moveStartX = posX;
            moveStartY = posY;
            Server.sync.updateMovement(this);
        }
    }

    /**
     * Lässt die Einheit auf einer linearen Strecke zum Ziel laufen.
     * Wenn die Bewegung nicht unterbrochen wird, und der Weg zum Ziel frei ist,
     * wird die Einheit exakt auf dem Ziel angehalten.
     * Staret eine neue Bewegung (Modus 2), stoppt aktuell laufende.
     *
     * @param tx Ziel-X
     * @param ty Ziel-Y
     * @param obs Observer
     */
    public void setLinearTarget(double tx, double ty, EntityLinearTargetObserver obs) {
        if (Double.isInfinite(tx) || Double.isInfinite(ty) || Double.isNaN(tx) || Double.isNaN(ty)) {
            throw new IllegalArgumentException("Illegal target!");
        }
        if (obs == null) {
            throw new IllegalArgumentException("Observer must not be null!");
        }
        if (remainingPathLength != -1) {
            observer.movementAborted();
        }
        // Sofort da?
        if (Math.abs(tx - getX()) < Settings.DOUBLE_EQUALS_DIST || Math.abs(ty - getY()) < Settings.DOUBLE_EQUALS_DIST) {
            // Gar nicht erst bewegen.
            obs.targetReached();
            return;
        }
        observer = obs;
        // Vektor zum Ziel und Länge berechnen:
        normalizeAndSetVector(tx - posX, ty - posY);
        remainingPathLength = Math.sqrt((tx - posX) * (tx - posX) + (ty - posY) * (ty - posY));
        targetX = tx;
        targetY = ty;
        moving = true;
        // Das ist eine neue Client-Bewegung
        moveStartTick = Server.game.getTick();
        moveStartX = posX;
        moveStartY = posY;
        Server.sync.updateMovement(this);
    }

    /**
     * Normalisiert den Vektor (x, y) und setzt ihn anschließend.
     *
     * @param x X-Richtung
     * @param y Y-Richtung
     */
    private void normalizeAndSetVector(double x, double y) {
        // Länge berechnen (Pythagoras)
        double length = Math.sqrt((x * x) + (y * y));
        // Normalisieren und setzen
        vecX = x / length;
        vecY = y / length;
    }

    public Movement getMovement() {
        return computeMovement();
    }

    private Movement computeMovement() {
        if (isMoving()) {
            return new Movement(moveStartX, moveStartY, vecX, vecY, moveStartTick, speed);
        } else {
            return new Movement(posX, posY, 0, 0, -1, 0);
        }
    }

    /**
     * Wie groß die Byte-Representation dieses Entitys ist. Die Größe darf 32 auf keinen Fall überschreiten! Implementierungen von Entity müssen diese Methode überschreiben und super.byteArraySize() +
     * Eigenbedarf zurückgeben!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 5;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses Chars in das gegebene Array. Das Array muss mindestens byteArraySize() + offset groß sein. Unterklassen müssen diese
     * Methode überschreiben, falls sie irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes oder ähnlich stehen. Überschriebene Methoden müssen erst super.netPack() aufrufen, und dann
     * selber den Puffer ab super.byteArraySize() + offset befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = entityTypeID;
        Bits.putInt(b, offset + 1, netID);
    }

    /**
     * Liefert die Richtung in die sich diese Einheit gerade bewegt. Die Angabe ist eine Fließkommazahl von 0 bis 2PI im üblichen Einheitskreisverfahren. Das Verhalten, wenn die Einheit sich nicht
     * bewegt ist nicht definiert.
     *
     * @return die Richtung in die sich diese Einheit gerade bewegt
     */
    public double getDirection() {
        double dir = Math.atan2(vecY, vecX);
        if (dir < 0) {
            dir += 2 * Math.PI;
        }
        return dir;
    }

    /**
     * Die aktuelle Positon auf der EntityMap.
     *
     * @return the entityMapPos
     */
    public int[] getEntityMapPos() {
        return entityMapPos;
    }

    /**
     * Die aktuelle Positon auf der EntityMap.
     *
     * @param entityMapPos the entityMapPos to set
     */
    public void setEntityMapPos(int[] entityMapPos) {
        this.entityMapPos = entityMapPos;
    }

    /**
     * @return the size
     */
    public double getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    protected void setSize(double size) {
        this.size = size;
    }

    /**
     * Berechnet einen gameTick für die Entity.
     */
    public void tick(int gameTick) {
        if (moving) { // Einheit bewegt sich
            double oldX = posX;
            double oldY = posY;
            double predictedX = posX + speed * vecX;
            double predictedY = posY + speed * vecY;
            // Bewegung einfach mal setzen, und dann die Kollission das prüfen lassen
            posX = predictedX;
            posY = predictedY;
            // Ist das weiter als wir wollen?
            boolean targetReached = false;
            if (remainingPathLength != -1) {
                remainingPathLength -= Math.sqrt((predictedX - oldX) * (predictedX - oldX) + (predictedY - oldY) * (predictedY - oldY));
                if (remainingPathLength <= 0) {
                    targetReached = true;
                    posX = targetX;
                    posY = targetY;
                    predictedX = posX;
                    predictedY = posY;
                }
            }
            int[] colBlock = computeCollision(oldX, oldY, posX, posY);
            // Hat sich unsere Bewegung geändert?
            if (posX != predictedX || posY != predictedY) {
                // Client(s) informieren
                Server.sync.updateMovement(this);
                // Unterklassen informieren
                onWallCollision(colBlock);
            } else if (targetReached) {
                // Wir sind am Ziel!
                remainingPathLength = -1;
                stopMovement();
                observer.targetReached();
            }
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
        // Den Block, mit dem wir kollidieren zwischenspeichern
        int[] collisionBlock = new int[2];
        // Jetzt alle Blöcke im angegebenen Gebiet checken:
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {

                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidX + (Settings.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;
                    d2 = ((blockMidX - (Settings.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromX) / deltaX;

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
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
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
        for (int x = moveAreaStartX; x < moveAreaEndX; x++) {
            for (int y = moveAreaStartY; y < moveAreaEndY; y++) {
                if (Server.game.getLevel().getCollisionMap()[x][y] == true) {


                    // Der Mittelpunkt des Blocks
                    blockMidX = x + 0.5;
                    blockMidY = y + 0.5;
                    // Wenn nicht müssen wir noch auf Y-Kollision prüfen:
                    // Die Faktoren für die beiden Punkte, an denen der Mover den Block berühren würde
                    d1 = ((blockMidY + (Settings.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
                    d2 = ((blockMidY - (Settings.DOUBLE_EQUALS_DIST + 0.5 + getSize() / 2.0)) - fromY) / deltaY;
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
                            collisionBlock[0] = x;
                            collisionBlock[1] = y;
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
        if (!(Double.isNaN(sx) && Double.isNaN(sy))) {
            setStopXY(sx, sy);
        }

        return collisionBlock;
    }

    /**
     * Wird aufgerufen, wenn diese Entity sich in eine andere bewegt.
     *
     * @param other die Entity, in die diese sich hineinbewegt hat
     */
    public void onCollision(Entity other) {
    }

    /**
     * Wird aufgerufen, wenn die Entity mit einer wand kollidiert.
     */
    public void onWallCollision(int[] collisionBlock) {
    }

    /**
     * Liefert den Vektor, in den sich diese Einheit gerade bewegt. Undefiniert, falls die Einheit sich nicht bewegt.
     *
     * @return X-Richtungsvektor
     */
    protected double getVecX() {
        return vecX;
    }

    /**
     * Liefert den Vektor, in den sich diese Einheit gerade bewegt. Undefiniert, falls die Einheit sich nicht bewegt.
     *
     * @return Y-Richtungsvektor
     */
    protected double getVecY() {
        return vecY;
    }
}
