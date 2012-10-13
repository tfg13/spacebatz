package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.entities.Char;
import java.util.HashMap;

/**
 * Verwaltet die Fähigkeiten eines Charakters.
 *
 * @author michael
 */
public class SpellBook {

    public final static byte SHIFTSKILL = 0;
    public final static byte SPACESKILL = 1;
    public final static byte CNTRLSKILL = 2;
    private HashMap<Byte, Ability> abilities;

    public SpellBook() {
        abilities = new HashMap<>();
    }

    public void useAbility(byte ability, double posX, double posY, Char user) {
        if (abilities.containsKey(ability)) {
            abilities.get(ability).useOnPosition(user, posX, posY);
        } else {
            throw new IllegalArgumentException("Char hat keine Fähigkeit " + ability + "!");
        }
    }

    /**
     * Belegt einen Abilityslot mit einer Ability.
     *
     * @param abilityId der AbilitySlot, z.B. SHIFTSKILL
     * @param ability die Fähigkeit
     */
    public void mapAbility(byte abilityId, Ability ability) {
        if (abilities.containsKey(abilityId)) {
            System.out.println("Waring: Ovverriding ability " + abilityId);
        } else {
            abilities.put(abilityId, ability);
        }
    }
}
