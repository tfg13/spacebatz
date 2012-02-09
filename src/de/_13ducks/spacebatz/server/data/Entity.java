package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Movement;

/**
 * Oberklasse für alle Objekte im Spiel Dazu gehören Chars, Mobs, Pflanzen, ... Enthält Position und Bewegungsinformationen
 *
 * @author michael
 */
public class Entity {

    /**
     * Die netID der Entity.
     */
    private int netID;
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
     * Der Sektor der EntityMap, in dem die Entity gerade registriert ist.
     */
    private EntityMapSector mySector;

    /**
     * Konstruktor, erstellt eine neue Entity
     *
     * @param x die X-Koordinate der Entity
     * @param y die Y-Koordinate der Entity
     * @param netID die netId der Entity
     */
    public Entity(double x, double y, int netID) {

        this.posX = x;
        this.posY = y;
        this.netID = netID;
    }

    public boolean isMoving() {
        return getMoveStartTick() != -1;
    }

    /**
     * Setzt die Stand-Position dieser Einheit. Falls die Einheit gerade steht, wird die Bewegung abgebrochen.
     *
     * @param x die neue X-Position.
     */
    public void setStillX(double x) {
        moveStartTick = -1;
        posX = x;
        movementDirty = true;
    }

    /**
     * Setzt die Stand-Position dieser Einheit. Falls die Einheit gerade steht, wird die Bewegung abgebrochen.
     *
     * @param x die neue X-Position.
     */
    public void setStillY(double y) {
        moveStartTick = -1;
        posY = y;
        movementDirty = true;
    }

    /**
     * Stoppt die Einheit sofort. Berechnet den Aufenthaltsort anhand des aktuellen Ticks. Die Bewegung ist danach beendet. Es passiert nichts, wenn die Einheit schon steht.
     */
    public void stopMovement() {
        posX = getX();
        posY = getY();
        moveStartTick = -1;
        movementDirty = true;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet Bewegungen anhand des aktuellen Gameticks mit ein.
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
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet Bewegungen anhand des aktuellen Gameticks mit ein.
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
     * Setzt den Bewegungsvektor dieses Chars neu. Die Einheit bewegt sich nach dem Aufruf in diese Richtung. Berechnet falls nötig die aktuelle Position zuerst neu. Der Vektor wird normalisiert, kann
     * also die Geschwindigkeit nicht beeinflussen. Das geht nur mit setSpeed. Die Werte dürfen nicht beide 0 sein!
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
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt. Initialisiert die Bewegung einer Einheit neu, damit Geschwindigkeitsänderungen während der Bewegung möglich sind. Sollte
     * daher wenn möglich vor dem Start der Bewegung aufgerufen werden.
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
        return getX() + vecX * getSpeed() * (Server.game.getTick() + ticks - getMoveStartTick());
    }

    /**
     * Extrapoliert die Bewegung dieses Chars, d.h. berechnet die Position für einige Ticks vorraus.
     *
     * @return die Y-Koordinate des Chars nach der angegebenen Zahl Ticks
     */
    public double extrapolateY(int ticks) {
        return getY() + vecY * getSpeed() * (Server.game.getTick() + ticks - getMoveStartTick());
    }

    /**
     * Gibt die netID der Entity zurück.
     *
     * @return die netID der Entity
     */
    public int getNetID() {
        return netID;
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
     * Gibt den Sektor, in dem die Entity gerade registriert ist, zurück.
     *
     * @return der Sektor der Entity
     */
    public EntityMapSector getSector() {
        return mySector;
    }

    /**
     * Setzt den Sektor, in dem die Entity gerade registriert ist.
     *
     * @param mySector der neue Sektor der Entity
     */
    public void setSector(EntityMapSector mySector) {
        this.mySector = mySector;
    }
}
