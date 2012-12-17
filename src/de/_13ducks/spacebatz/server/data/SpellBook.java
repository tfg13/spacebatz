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

    private HashMap<Byte, Ability> abilities;

    public SpellBook() {
        abilities = new HashMap<>();
    }

    public void useAbility(byte ability, double posX, double posY, Char user) {
        if (abilities.containsKey(ability)) {
            abilities.get(ability).tryUseOnPosition(user, posX, posY);
        } else {
            System.out.println("Char hat keine Fähigkeit " + ability + "!");
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
            System.out.println("Waring: Ovverriding ability " + abilityId + " with " + ability.toString());

        }
        abilities.put(abilityId, ability);

    }
}
