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
     * Gibt die Fähigkeit dieser Waffe zurück.
     *
     * @return die Fähigkeit dieser Waffe
     */
    public Ability getAbility() {
        return weaponAbility;
    }
}
