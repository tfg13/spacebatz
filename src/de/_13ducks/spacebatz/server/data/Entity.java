package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;

/**
 * Oberklasse für alle Objekte im Spiel Dazu gehören Chars, Mobs, Pflanzen, ... Enthält Position und Bewegungsinformationen
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
	Server.entityMap.insertEntity(this);
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
     * Stoppt die Einheit sofort. Berechnet den Aufenthaltsort anhand des aktuellen Ticks. Die Bewegung ist danach beendet. Es passiert nichts, wenn die Einheit
     * schon steht.
     */
    public void stopMovement() {
	posX = getX();
	posY = getY();
	moveStartTick = -1;
	movementDirty = true;
    }

    /**
     * Stoppt die Bewegung in X-Richtung. Beeinflusst eine Bewegung in Y-Richtung nicht. Wenn die Einheit (in X-Richtung) schon steht passiert nichts.
     */
    public void stopMovementX() {
	// Bewegen wir uns überhaupt (in X-Richtung)
	if (moveStartTick != -1 && vecX != 0) {
	    if (vecY != 0) { 
		setVector(0, vecY);
	    } else {
		stopMovement();
	    }
	}	
    }

    /**
     * Stoppt die Bewegung in Y-Richtung. Beeinflusst eine Bewegung in X-Richtung nicht. Wenn die Einheit (in Y-Richtung) schon steht passiert nichts.
     */
    public void stopMovementY() {
	// Bewegen wir uns überhaupt (in Y-Richtung)
	if (moveStartTick != -1 && vecY != 0) {
	    if (vecX != 0) { 
		setVector(vecX, 0);
	    } else {
		stopMovement();
	    }
	}	
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
     * Setzt den Bewegungsvektor dieses Chars neu. Die Einheit bewegt sich nach dem Aufruf in diese Richtung. Berechnet falls nötig die aktuelle Position zuerst
     * neu. Der Vektor wird normalisiert, kann also die Geschwindigkeit nicht beeinflussen. Das geht nur mit setSpeed. Die Werte dürfen nicht beide 0 sein!
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
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt. Initialisiert die Bewegung einer Einheit neu, damit Geschwindigkeitsänderungen
     * während der Bewegung möglich sind. Sollte daher wenn möglich vor dem Start der Bewegung aufgerufen werden.
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
     * Gibt den Gametick in dem die Bewegung gestartet wurde zurück
     *
     * @return der gametick in dem die Bewegung gestartet wurde
     */
    public int getMoveStartTick() {
	return moveStartTick;
    }

    /**
     * Wie groß die Byte-Representation dieses Entitys ist. Die Größe darf 32 auf keinen Fall überschreiten! Implementierungen von Entity müssen diese Methode
     * überschreiben und super.byteArraySize() + Eigenbedarf zurückgeben!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
	return 5;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses Chars in das gegebene Array. Das Array muss mindestens byteArraySize() + offset
     * groß sein. Unterklassen müssen diese Methode überschreiben, falls sie irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes oder ähnlich
     * stehen. Überschriebene Methoden müssen erst super.netPack() aufrufen, und dann selber den Puffer ab super.byteArraySize() + offset befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
	b[offset] = entityTypeID;
	Bits.putInt(b, offset + 1, netID);
    }
}
