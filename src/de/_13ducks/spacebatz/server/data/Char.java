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
     * Tpy des Chars. FÃ¼rs Netzwerksystem. 1 - Char (reserviert, eigentlich ein ungÃ¼ltiger Wert!) 2 - Player 3 - Enemy
     */
    public final byte charTypeID;
    /**
     * Die Lebenspunkte des Chars
     */
    protected int healthpoints;
    /**
     * Die maximalen Lebenspunkte des Chars
     */
    protected int healthpointsmax;
    /**
     * RÃ¼stung, verringert Schaden
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
     * Die ID des Bildes fÃ¼r den Char
     */
    protected int pictureID = 0;
    /**
     * Wie lange nach Angriff gewartet werden muss, bis wieder angegriffen werden darf
     */
    protected int attackcooldown = 5;
    /**
     * Der Tick, ab dem wieder geschossen werden darf
     */
    protected int attackcooldowntick;
    /**
     * Reichweite fÃ¼r Angriffe
     */
    protected double range;
    /**
     * Die GrÃ¶ÃŸe des Chars (fÃ¼r Kollision)
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
        this.healthpointsmax = 10;
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
     * @param e, Entity das Schaden zufÃ¼gt
     * @return true, wenn Enemy stirbt, sonst false
     */
    public boolean decreaseHealthpoints(Entity e) {
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
     * Wie groÃŸ die Byte-Representation dieses Chars ist. Die GrÃ¶ÃŸe darf 512 - 32 auf keinen Fall Ã¼berschreiten!
     *
     * @return die grÃ¶ÃŸe des byte[]'s, das netPack() braucht.
     */
    public int byteArraySize() {
        return 5;
    }

    /**
     * Schreibt die fÃ¼r eine NetzwerkÃ¼bertragung unbedingt nÃ¶tigen Werte dieses Chars in das gegebene Array. Das Array muss mindestens byteArraySize() + offset groÃŸ sein. Unterklassen mÃ¼ssen diese
     * Methode Ã¼berschreiben, falls sie irgendwelche zusÃ¤tzlichen Daten haben, die nicht in den Enemytypes oder Ã¤hnlich stehen. Ãœberschriebene Methoden mÃ¼ssen erst super.netPack() aufrufen, und dann
     * selber den Puffer ab super.byteArraySize() -1 + offset befÃ¼llen.
     *
     * @param b der Puffer, in den geschrieben ist.
     */
    public void netPack(byte[] b, int offset) {
        b[offset] = charTypeID;
        Bits.putInt(b, offset + 1, netID);
    }

    /**
     * Gibt die KollisionsgrÃ¶ÃŸe dieses Chars zurÃ¼ck
     *
     * @return die KollisionsgrÃ¶ÃŸe
     */
    public double getSize() {
        return size;
    }

    /**
     * @return the range
     */
    public double getRange() {
        return range;
    }

    /**
     * @return the armor
     */
    public int getArmor() {
        return armor;
    }
}
