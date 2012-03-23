package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.effects.AreaEffect;
import de._13ducks.spacebatz.server.data.effects.CharEffect;
import de._13ducks.spacebatz.server.data.effects.Effect;
import de._13ducks.spacebatz.server.data.effects.TemporaryStatBoost;
import java.util.ArrayList;

/**
 * Sprint-Fähigkeit, gibt dem Anwender einen Geschwindigkeitsbonus
 *
 * @author michael
 */
public class AbilitySprint extends Ability {

    /**
     * Die BonusEffekte
     *
     * Hier könnte z.B. eine Schockwelle rein, dann hätte man "Phasewalk" aus Borderlands(Siren)
     */
    private ArrayList<Effect> bonusEffects;
    private CharEffect speedBuffEffect;

    public AbilitySprint() {
        speedBuffEffect = new TemporaryStatBoost("speed", 10, 60);
        bonusEffects = new ArrayList<>();
    }

    @Override
    public void onUse() {
        //owner.applyEffect(speedBuffEffect);
        for (Effect e : bonusEffects) {
            if (e instanceof CharEffect) {
                ((CharEffect) e).applyToChar(owner);
            } else {
                ((AreaEffect) e).trigger(owner.getX(), owner.getY());
            }
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void addBonusEffect(Effect effect) {
        bonusEffects.add(effect);
    }
}
