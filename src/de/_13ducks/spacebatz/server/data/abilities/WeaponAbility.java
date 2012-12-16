package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.shared.WeaponStats;
import java.io.Serializable;

/**
 * Eine Fähigkeit, die WeaponStats hat die verbessert werden können.
 *
 * @author michael
 */
public abstract class WeaponAbility implements Ability, Serializable {

    /**
     * Die Daten der Waffe.
     */
    private WeaponStats weaponStats;

    /**
     * Erzeugt eine neue Waffenfähigkeit.
     */
    public WeaponAbility() {
        weaponStats = new WeaponStats();
    }

    /**
     * Fügt dieser Fähigkeit Waffenstats hinzu.
     *
     * @param stats
     */
    public void addWeaponStats(WeaponStats stats) {
        getWeaponStats().addStats(stats);
    }

    /**
     * @return the weaponStats
     */
    public WeaponStats getWeaponStats() {
        return weaponStats;
    }

    /**
     * @param weaponStats the weaponStats to set
     */
    public void setWeaponStats(WeaponStats weaponStats) {
        this.weaponStats = weaponStats;
    }
}
