package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.shared.WeaponStats;

/**
 * Eine Fähigkeit, die WeaponStats hat die verbessert werden können.
 *
 * @author michael
 */
public abstract class WeaponAbility extends WeaponStats implements Ability {

    /**
     * Fügt dieser Fähigkeit Waffenstats hinzu.
     *
     * @param stats
     */
    public void addWeaponStats(WeaponStats stats) {
        this.addStats(stats);
    }
}
