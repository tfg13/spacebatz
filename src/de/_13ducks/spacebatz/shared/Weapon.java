package de._13ducks.spacebatz.shared;


import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.ItemAttribute;

/**
 * Eine Waffe ist ein Item, das Stats gibt *und* einen Angriff ermöglicht.
 *
 * @author michael
 */
public class Weapon extends Item {

    private static final long serialVersionUID = 1L;
    /**
     * Die Fähigkeit, die diese Waffe gibt.
     */
    private Ability weaponAbility;

    /**
     * Erstellt eine neue Waffe.
     *
     * @param name der Name der Waffe
     * @param itemAttribute das Grundattribut der Waffe
     * @param x die X-Koordinate des Items
     * @param y die Y-Koordinate des Items
     * @param netID die netID der Waffe
     */
    public Weapon(String name, ItemAttribute itemAttribute, double x, double y, int netID, Ability weaponAbility) {
        super(name, itemAttribute, x, x, netID);
        this.weaponAbility = weaponAbility;
    }

    /**
     * Gibt die Waffenfähigkeit zurück.
     *
     * @return die Waffenfähigkeit
     */
    public Ability getWeaponAbility() {
        return weaponAbility;
    }
}
