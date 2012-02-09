package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.util.Bits;

/**
 * Ein bewegliches Objekt. (z.B. ein Spieler, Mob etc)
 *
 * @author michael
 */
public abstract class Char extends Entity {

    /**
     * Tpy des Chars. Fürs Netzwerksystem. 1 - Char (reserviert, eigentlich ein ungültiger Wert!) 2 - Player 3 - Enemy
     */
    public final byte charTypeID;
    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints;
    /**
     * Rüstung, verringert Schaden
     */
    protected int armor;
    /**
     * Der Schaden des Chars
     */
    protected int damage;
    /**
     * Die Sichtweite des Chars
     */
    protected int sightrange;
    /**
     * Die ID des Bildes für den Char
     */
    protected int pictureID = 0;
    /**
     * Wie lange nach Angriff gewartet werden muss, bis 
     */
    protected int attackcooldown = 5;
    /**
     * Der Tick, ab dem wieder geschossen werden darf
     */
    protected int attackcooldowntick;
    /**
     * Reichweite für Angriffe
     */
    protected double range;
    /**
     * Die Größe des Chars (für Kollision)
     */
    private double size;

    /**
     * Konstruktor, erstellt einen neuen Char
     *
     * @param x
     * @param y
     * @param name
     */
    public Char(double x, double y, int netID, byte charTypeID) {
        super(x, y, netID);
        this.charTypeID = charTypeID;
        size = Settings.CHARSIZE;
        this.healthpoints = 10;
        this.damage = 2;
        this.range = 1.0;
        this.sightrange = 10;
        this.attackcooldown = 10;
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
     * Wie groß die Byte-Representation dieses Chars ist. Die Größe darf 512 - 32 auf keinen Fall überschreiten!
     *
     * @return die größe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 5;
    }

    /**
     * Schreibt die für eine Netzwerkübertragung unbedingt nötigen Werte dieses Chars in das gegebene Array. Das Array muss mindestens byteArraySize() + offset groß sein. Unterklassen müssen diese
     * Methode überschreiben, falls sie irgendwelche zusätzlichen Daten haben, die nicht in den Enemytypes oder ähnlich stehen. Überschriebene Methoden müssen erst super.netPack() aufrufen, und dann
     * selber den Puffer ab super.byteArraySize() -1 + offset befüllen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = charTypeID;
        Bits.putInt(b, offset + 1, netID);
    }

    /**
     * Gibt die Kollisionsgröße dieses Chars zurück
     *
     * @return die Kollisionsgröße
     */
    public double getSize() {
        return size;
    }
}
