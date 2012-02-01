package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.shared.Movement;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein bewegliches Objekt. (z.B. ein Spieler, Mob etc)
 *
 * @author michael
 */
public abstract class Char {

    /**
     * Tpy des Chars. Fürs Netzwerksystem. 1 - Char (reserviert, eigentlich ein
     * ungültiger Wert!) 2 - Player 3 - Enemy
     */
    public final byte charTypeID;
    /**
     * Die ID des Chars.
     */
    public final int netID;
    /**
     * Die Position des Chars, solange er still steht. Die Startkoordinaten der
     * Bewegung, solange er sich bewegt.
     */
    protected double posX, posY;
    /**
     * Die Bewegungsgeschwindigkeit eines Chars. Einheit: Felder/Tick
     */
    protected double speed = .17;
    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints = 10;
    /**
     * Der Schaden des Chars
     */
    protected int damage = 1;
    /**
     * Die Sichtweite des Chars
     */
    protected int sightrange = 1;
    /**
     * Die ID des Bildes für den Char
     */
    protected int pictureID = 0;
    /**
     * Der Tick, bei dem die Bewegung gestartet wurde. -1, falls er sich nicht
     * bewegt.
     */
    protected int moveStartTick;
    /**
     * Der Tick, ab dem wieder geschossen werden darf
     */
    protected int AttackCooldownTick;
    /**
     * Die Richtung, in die die Einheit läuft. Normalisierte Werte! Nur
     * relevant, wenn moveStartTick != -1;
     */
    protected double vecX, vecY;
    /**
     * Die aktuelle Bewegung nocheinmal repräsentiert. Nur aktuell wenn
     * moveDirty == false.
     */
    private Movement movement;
    /**
     * Ob das aktuelle Movement stimmt.
     */
    private boolean movementDirty = true;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x
     * @param y
     * @param name
     */
    public Char(double x, double y, int netID, byte charTypeID) {
        this.charTypeID = charTypeID;
        this.posX = x;
        this.posY = y;
        this.netID = netID;
    }

    public boolean isMoving() {
        return moveStartTick != -1;
    }

    /**
     * Setzt die Stand-Position dieser Einheit. Falls die Einheit gerade steht,
     * wird die Bewegung abgebrochen.
     *
     * @param x die neue X-Position.
     */
    public void setStillX(double x) {
        moveStartTick = -1;
        posX = x;
        movementDirty = true;
    }

    /**
     * Setzt die Stand-Position dieser Einheit. Falls die Einheit gerade steht,
     * wird die Bewegung abgebrochen.
     *
     * @param x die neue X-Position.
     */
    public void setStillY(double y) {
        moveStartTick = -1;
        posY = y;
        movementDirty = true;
    }

    /**
     * Liefert die derzeitige Bewegungsrichtung dieser Einheit. Liefert null,
     * wenn die Einheit sich gerade nicht bewegt.
     *
     * @return das Bewegungsrichtung oder null
     */
    public double[] getVector() {
        if (isMoving()) {
            return new double[]{vecX, vecY};
        }
        return null;
    }

    /**
     * Stoppt die Einheit sofort. Berechnet den Aufenthaltsort anhand des
     * aktuellen Ticks. Die Bewegung ist danach beendet. Es passiert nichts,
     * wenn die Einheit schon steht.
     */
    public void stopMovement() {
        posX = getX();
        posY = getY();
        moveStartTick = -1;
        movementDirty = true;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet
     * Bewegungen anhand des aktuellen Gameticks mit ein.
     *
     * @return Die echte Position X dieses Chars.
     */
    public double getX() {
        if (isMoving()) {
            return posX + ((Server.game.getTick() - moveStartTick) * getSpeed() * vecX);
        }
        return posX;
    }

    /**
     * Liefert die aktuelle Aufenthaltsposition dieser Einheit. Berechnet
     * Bewegungen anhand des aktuellen Gameticks mit ein.
     *
     * @return Die echte Position X dieses Chars.
     */
    public double getY() {
        if (isMoving()) {
            return posY + ((Server.game.getTick() - moveStartTick) * getSpeed() * vecY);
        }
        return posY;
    }

    /**
     * Setzt den Bewegungsvektor dieses Chars neu. Die Einheit bewegt sich nach
     * dem Aufruf in diese Richtung. Berechnet falls nötig die aktuelle Position
     * zuerst neu. Der Vektor wird normalisiert, kann also die Geschwindigkeit
     * nicht beeinflussen. Das geht nur mit setSpeed. Die Werte dürfen nicht
     * beide 0 sein!
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
     * Setzt die Geschwindigkeit dieser Einheit. Es sind nur Werte > 0 erlaubt.
     * Initialisiert die Bewegung einer Einheit neu, damit
     * Geschwindigkeitsänderungen während der Bewegung möglich sind. Sollte
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
            movement = new Movement(posX, posY, vecX, vecY, moveStartTick, getSpeed());
        } else {
            movement = new Movement(posX, posY, 0, 0, -1, 0);
        }
    }

    /**
     * @return the healthpoints
     */
    public int getHealthpoints() {
        return healthpoints;
    }

    /**
     * Zieht Schadenspunkte von HP ab, returned true wenn Einheit stirbt
     *
     * @param b, Bullet das Schaden zufügt
     * @return true, wenn Enemy stirbt, sonst false
     */
    public boolean decreaseHealthpoints(Bullet b) {
        return false;
    }

    /**
     * @return the healthpoints
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return the pictureID
     */
    public int getPictureID() {
        return pictureID;
    }

    /**
     * @return the sigthrange
     */
    public int getSightrange() {
        return sightrange;
    }

    /**
     * Wie groß die Byte-Representation dieses Chars ist. Die Größe darf 512 -
     * 32 auf keinen Fall überschreiten!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 5;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses
     * Chars in das gegebene Array. Das Array muss mindestens byteArraySize() +
     * offset groß sein. Unterklassen müssen diese Methode überschreiben, falls
     * sie irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes
     * oder ähnlich stehen. Überschriebene Methoden müssen erst super.netPack()
     * aufrufen, und dann selber den Puffer ab super.byteArraySize() -1 + offset
     * befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = charTypeID;
        Bits.putInt(b, offset + 1, netID);
    }

    public void clientShoot(float angle) {
        int thistick = Server.game.getTick();
        if (thistick > AttackCooldownTick + 5) {
            AttackCooldownTick = thistick;


            Bullet bullet = new Bullet(thistick, getX(), getY(), angle, 0, Server.game.newNetID(), this);
            Server.game.bullets.add(bullet);
            byte[] bytearray = new byte[25];

            bytearray[0] = Settings.NET_UDP_CMD_SPAWN_BULLET;
            Bits.putInt(bytearray, 1, bullet.getSpawntick());
            Bits.putFloat(bytearray, 5, (float) bullet.getSpawnposX());
            Bits.putFloat(bytearray, 9, (float) bullet.getSpawnposY());
            Bits.putFloat(bytearray, 13, (float) bullet.getDirection());
            Bits.putInt(bytearray, 17, bullet.getTypeID());
            Bits.putInt(bytearray, 21, bullet.getNetID());

            for (int i = 0; i < Server.game.clients.size(); i++) {
                Server.serverNetwork.udp.sendPack(bytearray, Server.game.clients.get(i));
            }
        }
    }

    /**
     * Extrapoliert die Bewegung dieses Chars, d.h. berechnet die Position für
     * einige Ticks vorraus.
     *
     * @return die X-Koordinate des Chars nach der angegebenen Zahl Ticks
     */
    public double extrapolateX(int ticks) {
        return getX() + vecX * getSpeed() * (Server.game.getTick() + ticks - moveStartTick);
    }

    /**
     * Extrapoliert die Bewegung dieses Chars, d.h. berechnet die Position für
     * einige Ticks vorraus.
     *
     * @return die Y-Koordinate des Chars nach der angegebenen Zahl Ticks
     */
    public double extrapolateY(int ticks) {
        return getY() + vecY * getSpeed() * (Server.game.getTick() + ticks - moveStartTick);
    }
}
