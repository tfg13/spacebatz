package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.entities.Char;
import java.util.HashMap;

/**
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
            abilities.get(ability).useOnPosition(user, posX, posY);
        } else {
            throw new IllegalArgumentException("Char hat keine Fähigkeit " + ability + "!");
        }
    }

    public void setAbility(byte abilityId, Ability ability) {
        if (abilities.containsKey(abilityId)) {
            System.out.println("Waring: Ovverriding ability " + abilityId);
        } else {
            abilities.put(abilityId, ability);
        }
    }
}
