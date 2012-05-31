package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TrueDamageEffect;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.EffectCarrier;
import de._13ducks.spacebatz.server.gamelogic.CollisionManager;
import de._13ducks.spacebatz.server.gamelogic.DropManager;
import de._13ducks.spacebatz.util.Position;
import java.util.ArrayList;

/**
 * Eine Fähigkeit, die in eine Richtung angreift und sofort Schaden macht
 *
 * @author Jojo
 */
public class HitscanAbility extends Ability {

    private static final long serialVersionUID = 1L;
    /**
     * Die Effekte, die dieses Geschoss hat.
     */
    private ArrayList<Effect> effects = new ArrayList<>();
    
    public HitscanAbility(double damage, double attackspeed, double range) {
        TrueDamageEffect damageeff = new TrueDamageEffect((int) damage);
        effects.add(damageeff);
        setBaseProperty("range", range);
        setBaseProperty("attackspeed", attackspeed);
        
    }

    /**
     * Benutzt die Fähigkeit in einem bestimmten Winkel.
     *
     * @param user der Benutzerchar
     */
    @Override
    public void useInAngle(Char user, double angle) {
        
        double range = getProperty("range");
        double attackspeed = getProperty("attackspeed");
        if (user.attackCooldownTick <= Server.game.getTick()) {
            user.attackCooldownTick = Server.game.getTick() + (int) attackspeed;

            // Schaden an Gegnern
            ArrayList<Char> charsHit = CollisionManager.computeHitscanOnChars(user, angle, range, this);
            
            for (Char character : charsHit) {
                for (Effect effect : effects) {
                    effect.applyToChar((EffectCarrier) character);
                }
                
                if (character.getProperty("hitpoints") <= 0) {
                    Server.game.netIDMap.remove(character.netID);
                    Server.entityMap.removeEntity(character);
                    DropManager.dropItem(character.getX(), character.getY(), 2);
                }
            }

            // Block abbauem
            Position test = CollisionManager.computeHitscanOnBlocks(user, angle, user.getProperty("range"));
            if (test != null) {
                if (Server.game.getLevel().isBlockDestroyable(test.getX(), test.getY())) {
                    Server.game.getLevel().destroyBlock(test.getX(), test.getY());
                }
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
