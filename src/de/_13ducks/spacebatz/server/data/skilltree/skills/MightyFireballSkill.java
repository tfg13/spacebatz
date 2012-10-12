package de._13ducks.spacebatz.server.data.skilltree.skills;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTreeEntry;

/**
 * Mächtiger Feuerball.
 *
 * @author michael
 */
public class MightyFireballSkill extends SkillTreeEntry {

    private FireBulletAbility ability;

    public MightyFireballSkill() {
        super("MightyFireBallSkill");
        ability = new FireBulletAbility(100, 100, 100, 100, 100, 100, 100);
    }

    @Override
    public boolean canInvestPoint(SkillTree tree) {
        return true;
    }

    @Override
    public void onInvestPoint() {
        // ability.increaseDamage(100);
    }

    @Override
    public Ability getAbility() {
        return ability;
    }
}
