package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.shared.Properties;
import java.util.ArrayList;

/**
 * Eine Fähigkeit, die in eine Richtung angreift und sofort Schaden macht
 *
 * @author Jojo
 */
public class HitscanAbility extends Ability {

    /**
     * Die Effekte, die dieses Geschoss hat.
     */
    private ArrayList<Effect> effects = new ArrayList<>();

    public HitscanAbility(double damage, double attackspeed, double range) {
        TrueDamageEffect damageeff = new TrueDamageEffect((int) damage);
        effects.add(damageeff);
        addBaseProperty("attackspeed", attackspeed);
        addBaseProperty("range", range);
    }

    @Override
    public void refreshProperties(Properties properties) {
        // Werte des Trägers werden ignoriert.
    }

    @Override
    public void use() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useOnPosition(double targetX, double targetY) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useOnTarget(Entity target) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void useInAngle(double angle) {
        if (owner.getAttackCooldownTick() <= Server.game.getTick()) {
            owner.setAttackCooldownTick(Server.game.getTick() + (int) getBaseProperty("attackspeed"));

            ArrayList<Char> charsHit = CollisionManager.computeHitscanCollision(owner, angle, getBaseProperty("range"), this);

            for (Char character : charsHit) {
                for (Effect effect : effects) {
                    effect.applyToChar(character);
                }

                if (character.getProperty("hitpoints") <= 0) {
                    Server.game.netIDMap.remove(character.netID);
                    Server.entityMap.removeEntity(character);
                    DropManager.dropItem(character.getX(), character.getY(), 2);
                }
            }
        }
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
