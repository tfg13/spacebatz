package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.util.geo.GeoTools;
import de._13ducks.spacebatz.util.geo.IntVector;
import java.util.ArrayList;

/**
 * Eine Fähigkeit, die in eine Richtung angreift und sofort Schaden macht
 *
 * @author Jojo
 */
public class HitscanAbility extends WeaponAbility {

    private static final long serialVersionUID = 1L;
    /**
     * Die Effekte, die dieses Geschoss hat.
     */
    private ArrayList<Effect> effects = new ArrayList<>();
    //private double damage;

    public HitscanAbility(double damage, double damagespread, double attackspeed, double range, double maxoverheat, double reduceoverheat) {
        getWeaponStats().setDamage(damage);
        getWeaponStats().setDamagespread(damagespread);
        getWeaponStats().setAttackspeed(attackspeed);
        getWeaponStats().setRange(range);
        getWeaponStats().setMaxoverheat(maxoverheat);
        getWeaponStats().setReduceoverheat(reduceoverheat);
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        double angle = GeoTools.toAngle(x - user.getX(), y - user.getY());
        STC_CHAR_ATTACK.sendCharAttack(user.netID, (float) angle, true);
        double range = getWeaponStats().getRange();

        // Schaden an Gegnern
        ArrayList<Char> charsHit = CollisionManager.computeHitscanOnChars(user, angle, range, this);

        TrueDamageEffect damageeff = new TrueDamageEffect((int) ((getWeaponStats().getDamage() + getWeaponStats().getDamagespread() * 2 * (Math.random() - 0.5)) * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getWeaponStats().getDamageMultiplicatorBonus())));
        effects.clear();
        effects.add(damageeff);

        for (Char character : charsHit) {
            for (Effect effect : effects) {
                effect.applyToChar(character);
            }
        }

        // Block abbauen
        IntVector test = CollisionManager.computeHitscanOnBlocks(user, angle, getWeaponStats().getRange());
        if (test != null) {
            if (Server.game.getLevel().isBlockDestroyable(test.x, test.y)) {
                Server.game.getLevel().destroyBlock(test.x, test.y);
            }
        }
    }
}
