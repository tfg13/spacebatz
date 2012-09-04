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
 * Oberklasse für alle Objekte im Spiel Dazu gehören Chars, Mobs, Pflanzen, ... Enthält Position und
 * Bewegungsinformationen
 *
 * @author michael
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
     * Die Bewegungsgeschwindigkeit eine Entity. Einheit: Felder/Tick
     */
    private double posX;
    /**
     * Der Tick, bei dem die Bewegung gestartet wurde. -1, falls er sich nicht bewegt.
     */
    private double posY;
    /**
     * Die Geschwindigkeit der Bewegung
     */
    protected double speed = .17;
    /**
     * Der Gametick in dem die Bewegung gestartet wurde
     */
    protected int moveStartTick;
    /**
     * Die X-Koordinate des Bewegungsvektors
     */
    protected double vecX;
    /**
     * Die Y-Koordinate des Bewegungsvektors
     */
    protected double vecY;
    /**
     * Die aktuelle Bewegung nocheinmal repräsentiert. Nur aktuell wenn moveDirty == false.
     */
    private Movement movement;
    /**
     * Ob das aktuelle Movement stimmt.
     */
    private boolean movementDirty = true;
    /**
     * Die aktuelle Positon auf der EntityMap.
     */
    private int[] entityMapPos;
    /**
     * Die Größe der Entity für Kollisionsberechnung.
     */
    private double size;
    private boolean isDisposable;

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
        Server.entityMap.insertEntity(this);
        this.netID = netID;
        size = Settings.CHARSIZE;
    }

    /**
     * Liefert true, wenn die Einheit sich gerade bewegt.
     *
     * @return true, wenn die Einheit sich gerade bewegt
     */
    public boolean isMoving() {
        return getMoveStartTick() != -1;
    }

    /**
     * Setzt die Position dieser Einheit auf den angegebenen Wert. Wenn entweder x oder y (nicht beide!) NaN sind, wird
     * die Bewegung nur in eine Richtung angehalten. Darf nicht aufgerufen werden, wenn sich die Einheit gar nicht
     * bewegt.
     *
     * @param x die X-Stop-Koordinate oder NaN
     * @param y die Y-Stop-Koordinate oder NaN
     */
    public void setStopXY(double x, double y) {
        /*
         * Diese Methode ist nicht (!) das gleiche wie das alte setStillX und danach setStillY.
         * Das konnte böse Wechselwirkungsfehler erzeugen, die dazu führen, dass Einheiten in der Wand stecken bleiben.
         */
        boolean xCont = Double.isNaN(x);
        boolean yCont = Double.isNaN(y);
        if (xCont && yCont) {
            throw new IllegalArgumentException("Cannot setStop without stopping at all! (x=y=NaN)");
        }
        if (moveStartTick == -1) {
            throw new IllegalArgumentException("Cannot setStop, Entity is not moving at all!");
        }
        if (xCont) {
            posY = y;
            vecY = 0;
            if (vecX == 0) {
                moveStartTick = -1;
            }
        } else if (yCont) {
            posX = x;
            vecX = 0;
            if (vecY == 0) {
                moveStartTick = -1;
            }
        } else {
            stopMovement();
            posX = x;
            posY = y;
        }
        movementDirty = true;
    }

    /**
     * Stoppt die Einheit sofort. Berechnet den Aufenthaltsort anhand des aktuellen Ticks. Die Bewegung ist danach
     * beendet. Es passiert nichts, wenn die Einheit schon steht. Nicht zur Kollisionsberechnung geeignet, da die
     * Einheit die Bewegung dieses Ticks noch vollständig ausführt. Besser setStop verwenden.
     */
    public void stopMovement() {
        posX = getX();
        posY = getY();
        moveStartTick = -1;
        movementDirty = true;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet Bewegungen anhand des aktuellen Gameticks mit
     * ein.
     *
     * @return Die echte Position X dieses Chars.
     */
    public double getX() {
        if (isMoving()) {
            return posX + ((Server.game.getTick() - getMoveStartTick()) * getSpeed() * vecX);
        }
        return posX;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet Bewegungen anhand des aktuellen Gameticks mit
     * ein.
     *
     * @return Die echte Position X dieses Chars.
     */
    public double getY() {
        if (isMoving()) {
            return posY + ((Server.game.getTick() - getMoveStartTick()) * getSpeed() * vecY);
        }
        return posY;
    }

    /**
     * Setzt den Bewegungsvektor dieses Chars neu. Die Einheit bewegt sich nach dem Aufruf in diese Richtung. Berechnet
     * falls nötig die aktuelle Position zuerst neu. Der Vektor wird normalisiert, kann also die Geschwindigkeit nicht
     * beeinflussen. Das geht nur mit setSpeed. Die Werte dürfen nicht beide 0 sein!
     */
    public void setVector(double x, double y) {
        if (x == 0 && y == 0) {
            throw new IllegalArgumentException("Cannot set moveVector, x = y = 0 is not allowed!");
        }
        if (isMoving()) {
            stopMovement();
        }
        normalizeAndSetVector(x, y);
        moveStartTick = Server.game.getTick();
        movementDirty = true;
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
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt. Initialisiert die Bewegung einer Einheit
     * neu, damit Geschwindigkeitsänderungen während der Bewegung möglich sind. Sollte daher wenn möglich vor dem Start
     * der Bewegung aufgerufen werden.
     *
     * @param speed die neue Geschwindigkeit > 0
     */
    public void setSpeed(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("Cannot set speed: Must be greater than zero");
        }
        if (isMoving()) {
            stopMovement();
            this.speed = speed;
            setVector(vecX, vecY);
            movementDirty = true;
        } else {
            this.speed = speed;
        }
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
        if (movementDirty) {
            computeMovement();
        }
        return movement;
    }

    private void computeMovement() {
        if (isMoving()) {
            movement = new Movement(posX, posY, vecX, vecY, getMoveStartTick(), getSpeed());
        } else {
            movement = new Movement(posX, posY, 0, 0, -1, 0);
        }
    }

    /**
     * Extrapoliert die Bewegung dieses Chars, d.h. berechnet die Position für einige Ticks vorraus.
     *
     * @return die X-Koordinate des Chars nach der angegebenen Zahl Ticks
     */
    public double extrapolateX(int ticks) {
        return this.posX + vecX * getSpeed() * (Server.game.getTick() + ticks - getMoveStartTick());
    }

    /**
     * Extrapoliert die Bewegung dieses Chars, d.h. berechnet die Position für einige Ticks vorraus.
     *
     * @return die Y-Koordinate des Chars nach der angegebenen Zahl Ticks
     */
    public double extrapolateY(int ticks) {
        return this.posY + vecY * getSpeed() * (Server.game.getTick() + ticks - getMoveStartTick());
    }

    /**
     * Gibt den Gametick in dem die Bewegung gestartet wurde zurück
     *
     * @return der gametick in dem die Bewegung gestartet wurde
     */
    public int getMoveStartTick() {
        return moveStartTick;
    }

    /**
     * Wie groß die Byte-Representation dieses Entitys ist. Die Größe darf 32 auf keinen Fall überschreiten!
     * Implementierungen von Entity müssen diese Methode überschreiben und super.byteArraySize() + Eigenbedarf
     * zurückgeben!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 5;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses Chars in das gegebene Array. Das Array
     * muss mindestens byteArraySize() + offset groß sein. Unterklassen müssen diese Methode überschreiben, falls sie
     * irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes oder ähnlich stehen. Überschriebene Methoden
     * müssen erst super.netPack() aufrufen, und dann selber den Puffer ab super.byteArraySize() + offset befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = entityTypeID;
        Bits.putInt(b, offset + 1, netID);
    }

    /**
     * Liefert die Richtung in die sich diese Einheit gerade bewegt. Die Angabe ist eine Fließkommazahl von 0 bis 2PI im
     * üblichen Einheitskreisverfahren. Das Verhalten, wenn die Einheit sich nicht bewegt ist nicht definiert.
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
     * Gibt an, ob die Entity gelöscht werden darf
     *
     * @return the isDisposable
     */
    public boolean isDisposable() {
        return isDisposable;
    }

    /**
     * Markiert die Entity zum Löschen
     *
     * @param isDisposable the isDisposable to set
     */
    public void dispose() {
        this.isDisposable = true;
    }

    /**
     * Berechnet einen gameTick für die Entity.
     */
    public void tick() {
    }
}
