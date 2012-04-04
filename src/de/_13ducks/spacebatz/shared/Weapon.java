package de._13ducks.spacebatz.shared;

import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.server.data.abilities.Ability;

/**
 * Eine Waffe, im Prinzip ein Item das eine Fähigkeit hat, wie zB Bullets schiesen.
 *
 * @author michael
 */
public class Weapon extends Item {

    private static final long serialVersionUID = 1L;
    /**
     * Die Fähigkeit dieser Waffe
     */
    private Ability weaponAbility;

    /**
     * Erzeugt eine neue Waffe
     *
     * @param name der Name des Waffen-Items
     * @param posX die X-Position des Items
     * @param posY die Y-Position des Items
     * @param netId die netId des Items
     * @param ability die Fähigkeit dieser Waffe
     */
    public Weapon(String name, double posX, double posY, int netId, Ability ability) {
        super(name, posX, posY, netId);
        weaponAbility = ability;
    }

    /**
     * Benutzt die Waffe.
     */
    public void use() {
        weaponAbility.use();
    }

    /**
     * Benutzt die Waffe auf eine Position
     *
     * @param targetX die X-Koordinate des Ziels
     * @param targetY die Y-Koordinate des Ziels
     */
    public void useOnPosition(double targetX, double targetY) {
        weaponAbility.useOnPosition(targetX, targetY);
    }

    /**
     * Benutzt die Waffe auf eine Entity
     *
     * @param target die Entity auf die die Waffe benutzt werden soll
     */
    public void useOnTarget(Entity target) {
        weaponAbility.useOnTarget(target);
    }

    /**
     * Benutzt die Waffe in eine Richtung
     *
     * @param angle der Winkel, in den die Waffe benutzt werden soll
     */
    public void useInAngle(double angle) {
        weaponAbility.useInAngle(angle);
    }

    /**
     * Gibt true zurück, wenn alle Bedingungen erfüllt sind um diese Waffe zu verwenden.
     * (z.B. Cooldown, Energiekosten, ...)
     *
     * @return true, wenn die Waffe benutzt werden kann
     */
    public boolean isReady() {
        return weaponAbility.isReady();
    }
}
