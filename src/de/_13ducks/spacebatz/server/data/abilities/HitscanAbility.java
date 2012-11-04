package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
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
        setRange(range);
        setAttackspeed(attackspeed);
        this.damage = damage;
        setMaxoverheat(maxoverheat);
        setReduceoverheat(reduceoverheat);
    }

    /**
     * Benutzt die Fähigkeit in einem bestimmten Winkel.
     *
     * @param user der Benutzerchar
     */
    @Override
    public void useInAngle(Char user, double angle) {

        double range = getRange();

        // Schaden an Gegnern
        ArrayList<Char> charsHit = CollisionManager.computeHitscanOnChars(user, angle, range, this);

        TrueDamageEffect damageeff = new TrueDamageEffect((int) (damage * (1 + user.getProperties().getDamageMultiplicatorBonus()) * (1 + getDamageMultiplicatorBonus())));
        effects.clear();
        effects.add(damageeff);

        for (Char character : charsHit) {
            for (Effect effect : effects) {
                effect.applyToChar((Char) character);
            }

            if (character.getProperties().getHitpoints() <= 0) {
                Server.game.getEntityManager().removeEntity(character.netID);
                if (character instanceof Enemy) {
                    Enemy e = (Enemy) character;
                    DropManager.dropItem(e.getX(), e.getY(), e.getEnemylevel());
                }
            }
        }

        // Block abbauem
        Position test = CollisionManager.computeHitscanOnBlocks(user, angle, getRange());
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
