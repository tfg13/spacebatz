package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.shared.Properties;
import java.util.ArrayList;

/**
 * Eine Fähigkeit, die in eine Richtung angreift und sofort Schaden macht
 * 
 * @author Jojo
 */
public class HitscanAbility extends Ability {

    private double attackspeed;
    private double range;
    /**
     * Die Effekte, die dieses Geschoss hat.
     */
    private ArrayList<Effect> effects = new ArrayList<>();

    public HitscanAbility(double damage, double attackspeed, double range) {
        TrueDamageEffect damageeff = new TrueDamageEffect((int) damage);
        effects.add(damageeff);
        this.attackspeed = attackspeed;
        this.range = range;
    }

    @Override
    public void refreshProperties(Properties properties) {
        throw new UnsupportedOperationException("Not supported yet.");
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
            owner.setAttackCooldownTick(Server.game.getTick() + (int) attackspeed);

            CollisionManager.computeHitscanCollision(owner, angle, range, this);
        }
    }

    @Override
    public boolean isReady() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void applyEffectsToChar(Char target) {
        for (Effect effect : effects) {
            effect.applyToChar(target);
        }
    }
}