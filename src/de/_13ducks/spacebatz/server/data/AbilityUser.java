package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import java.util.HashMap;

/**
 * Ein Char, der Fähigkeiten verwenden kann.
 *
 * @author michael
 */
public class AbilityUser extends Char {

    public static final int ACTIVEWEAPONABILITY = 0;
    /**
     * Die Liste aller Fähigkeiten, die der Char hat.
     * Chars können höchstens eine Fähigkeit pro FähigkeitenTyp haben!
     */
    private HashMap<Integer, Ability> abilities;

    /**
     * Erzeug einen neuen AbilityUser
     *
     * @param x die X-Koordinate des AbilityUsers
     * @param y die Y-Koordinate des AbilityUsers
     * @param netId die netId des AbilityUsers
     * @param typeId die typeId des AbilityUsers
     */
    public AbilityUser(double x, double y, int netId, byte typeId) {
        super(x, y, netId, typeId);
        abilities = new HashMap<>();
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars.
     *
     * @param ability die ID der Ability
     */
    public void useAbility(int ability) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).use();
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars auf eine Position.
     *
     * @param ability die ID der Ability
     * @param x X-Koordinate der Zielposition
     * @param y Y-Koordinate der Zielposition
     */
    public void useAbilityOnPosition(int ability, double x, double y) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useOnPosition(x, y);
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars in eine bestimmte Richtung.
     *
     * @param ability die ID der Ability
     * @param angle die Richtung in die die Fähigkeit benutzt werden soll
     */
    public void useAbilityInAngle(int ability, double angle) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useInAngle(angle);
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars auf eine Zielentity.
     *
     * @param ability die ID der Ability
     * @param target das Ziel, auf das fie Fähigkeit benutzt werden soll
     */
    public void useAbilityOnTarget(int ability, Entity target) {
        if (!abilities.containsKey(ability)) {
            throw new IllegalArgumentException("No such Ability!");
        } else {
            abilities.get(ability).useOnTarget(target);
        }
    }

    /**
     * Gibt dem Char eine neue Fähigkeit.
     *
     * @param id der Typ der neuen Fähigkeit
     * @param ability die neue Fähigkeit
     */
    public void setAbility(int id, Ability ability) {
        if (abilities.containsKey(id)) {
            throw new IllegalArgumentException("There is already a ability with that id!");
        } else {
            abilities.put(id, ability);
            ability.setOwner(this);
        }
    }

    /**
     * Lädt die Stats des Besitzers neu, falls sich etwas geändert hat was die Fähigkeiten beeinflusst.
     */
    public void refreshAbilities() {
        for (Ability ability : abilities.values()) {
            ability.refreshProperties();
        }
    }
}
