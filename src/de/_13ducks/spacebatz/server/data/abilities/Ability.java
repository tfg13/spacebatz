package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Entity;

/**
 * Superklasse für alle Fähigkeiten.
 * Fähigkeiten sind alle Aktionen, wie z.B. Schießen, Bohren und Sprinten
 *
 * @author michael
 */
public abstract class Ability {

    /**
     * Die ID der Schießen-Fähigkeit
     */
    public static final int SHOOT = 1;
    /**
     * Der Charakter, der diese Fähigkeit "besitzt"
     */
    protected Char owner;

    /**
     * Setzt den Besitzer dieser Fähigkeit.
     * Der Besitzer muss gesetzt werden, bevor die Fähigkeit eingesetzt werden kann!
     *
     * @param owner der Char, der die Fähigkeit besitzt
     */
    public void setOwner(Char owner) {
        this.owner = owner;
    }

    /**
     * Liest die Eigenschaften des BesitzerChars neu ein.
     * Muss immer aufgerufen werden, wenn sich die Eigenschaften des BesitzerChars ändern oder die Fähigkeit einem
     * (anderen) Char zugewiesen wird.
     */
    public abstract void refreshProperties();

    /**
     * Benutzt die Fähigkeit.
     */
    public abstract void use();

    /**
     * Benutzt die Fähigkeit auf eine Position
     *
     * @param targetX die X-Koordinate des Ziels
     * @param targetY die Y-Koordinate des Ziels
     */
    public abstract void useOnPosition(double targetX, double targetY);

    /**
     * Benutzt die Fähigkeit auf eine Entity
     *
     * @param target die Entity auf die die Fähigkeit benutzt werden soll
     */
    public abstract void useOnTarget(Entity target);

    /**
     * Benutzt die Fähigkeit in eine Richtung
     *
     * @param angle der Winkel, in den die Fähigkeit benutzt werden soll
     */
    public abstract void useInAngle(double angle);

    /**
     * Gibt true zurück, wenn alle Bedingungen erfüllt sind um diese Fähigkeit zu verwenden.
     * (z.B. Cooldown, Energiekosten, ...)
     *
     * @return true, wenn die Fähigkeit benutzt werden kann
     */
    public abstract boolean isReady();
}
