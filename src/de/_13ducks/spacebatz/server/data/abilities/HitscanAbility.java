package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.util.Position;
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
    private double damage;

    public HitscanAbility(double damage, double attackspeed, double range, double maxoverheat, double reduceoverheat) {
        getWeaponStats().setRange(range);
        getWeaponStats().setAttackspeed(attackspeed);
        this.damage = damage;
        getWeaponStats().setMaxoverheat(maxoverheat);
        getWeaponStats().setReduceoverheat(reduceoverheat);
    }

    /**
     * Benutzt die Fähigkeit in einem bestimmten Winkel.
     *
     * @param user der Benutzerchar
     */
    @Override
    public void useInAngle(Char user, double angle) {
        STC_CHAR_ATTACK.sendCharAttack(user.netID, (float) angle);

        double range = getWeaponStats().getRange();

        // Schaden an Gegnern
        ArrayList<Char> charsHit = CollisionManager.computeHitscanOnChars(user, angle, range, this);

        TrueDamageEffect damageeff = new TrueDamageEffect((int) (damage * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getWeaponStats().getDamageMultiplicatorBonus())));
        effects.clear();
        effects.add(damageeff);

        for (Char character : charsHit) {
            for (Effect effect : effects) {
                effect.applyToChar((Char) character);
            }
        }

        // Block abbauem
        Position test = CollisionManager.computeHitscanOnBlocks(user, angle, getWeaponStats().getRange());
        if (test != null) {
            if (Server.game.getLevel().isBlockDestroyable(test.getX(), test.getY())) {
                Server.game.getLevel().destroyBlock(test.getX(), test.getY());
            }
        }

    }

    @Override
    public void useOnPosition(Char user,
            double x,
            double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
