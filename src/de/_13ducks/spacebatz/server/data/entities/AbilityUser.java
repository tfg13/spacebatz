package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;

/**
 * Ein Char, der Fähigkeiten verwenden kann.
 *
 * @author michael
 */
public class AbilityUser extends Char {

    /**
     * Der Tick, ab dem das nächste mal geschossen werden darf
     */
    private int attackCooldownTick;

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

    }

    /**
     * Benutzt eine Fähigkeit dieses Chars auf eine Position.
     *
     * @param ability die ID der Ability
     * @param x X-Koordinate der Zielposition
     * @param y Y-Koordinate der Zielposition
     */
    public void useAbilityOnPosition(int ability, double x, double y) {
        switch (ability) {
            case FireBulletAbility.FIREBULLETABILITY:
                if (getProperty("canShoot") != 0) {
                    FireBulletAbility.fireBullet(this, x, y);
                }
                break;
        }
    }

    /**
     * Benutzt eine Fähigkeit in einem Winkel
     *
     * @deprecated
     */
    public void useAbilityInAngle(int ability, double angle) {
        switch (ability) {
            case FireBulletAbility.FIREBULLETABILITY:
                if (getProperty("canShoot") != 0) {
                    FireBulletAbility.fireBulletInAngle(this, angle);
                }
                break;
        }
    }

    /**
     * @return the attackCooldownTick
     */
    public int getAttackCooldownTick() {
        return attackCooldownTick;
    }

    /**
     * @param attackCooldownTick the attackCooldownTick to set
     */
    public void setAttackCooldownTick(int attackCooldownTick) {
        this.attackCooldownTick = attackCooldownTick;
    }
}
