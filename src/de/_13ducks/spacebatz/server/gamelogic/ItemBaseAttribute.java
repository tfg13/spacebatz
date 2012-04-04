package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.shared.ItemAttribute;

/**
 * Das Grundattribut eines Items
 *
 * @author michael
 */
public class ItemBaseAttribute extends ItemAttribute {

    private static final long serialVersionUID = 1L;
    /**
     * Die Fähigkeit, die dieses Item gibt.
     * Kann null sein wenn dieses Item keine Fähigkeit gibt
     */
    private Ability ability;

    public ItemBaseAttribute(String name) {
        super(name);
    }

    /**
     * Gibt die Fähigkeit, die dieses Item gibt, zurück.
     * Kann null sein wenn dieses Item keine Fähigkeit gibt
     *
     * @return die Fähigkeit, die dieses Item gibt oder null wenn es keine gibt
     */
    public Ability getAbility() {
        return ability;
    }

    /**
     * Setzt die Fähigkeit, die dieses Item gibt.
     * Kann null sein wenn dieses Item keine Fähigkeit gibt
     *
     * @param ability die Fähigkeit, die das Item gebebn soll oder null wenns keine geben soll
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
}
