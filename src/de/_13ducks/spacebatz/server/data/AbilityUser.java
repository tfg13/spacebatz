package de._13ducks.spacebatz.server.data;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.shared.Properties;
import java.util.HashMap;

/**
 * Ein Char, der Fähigkeiten verwenden kann.
 *
 * @author michael
 */
public class AbilityUser extends Entity {

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
        if (abilities.get(ability) != null) {
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
        if (abilities.get(ability) != null) {
            abilities.get(ability).useOnPosition(vecX, vecY);
        }
    }

    /**
     * Benutzt eine Fähigkeit dieses Chars in eine bestimmte Richtung.
     *
     * @param ability die ID der Ability
     * @param angle die Richtung in die die Fähigkeit benutzt werden soll
     */
    public void useAbilityInAngle(int ability, double angle) {
        if (abilities.get(ability) != null) {
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
        if (abilities.get(ability) != null) {
            abilities.get(ability).useOnTarget(target);
        }
    }

    /**
     * Setzt die Fähigkeit mit der angegebenen ID. Kann auch auf null gesetzt werden.
     *
     * @param id der Typ der neuen Fähigkeit
     * @param ability die neue Fähigkeit
     */
    public void setAbility(int id, Ability ability) {
        if (ability == null) {
            abilities.put(id, null);
        } else {
            abilities.put(id, ability);
            ability.setOwner(this);
        }
    }

    /**
     * Lädt die Stats des Besitzers neu, falls sich etwas geändert hat was die Fähigkeiten beeinflusst.
     *
     * @param properties die Properties des Besitzers
     */
    public void refreshAbilities(Properties properties) {
        for (Ability ability : abilities.values()) {
            if (ability != null) {
                ability.refreshProperties(properties);
            }
        }
    }
}
